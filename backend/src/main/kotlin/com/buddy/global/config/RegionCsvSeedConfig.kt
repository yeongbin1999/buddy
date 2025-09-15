package com.buddy.global.config

import com.buddy.domain.region.entity.RegionMunicipality
import com.buddy.domain.region.entity.RegionProvince
import com.buddy.domain.region.repository.RegionMunicipalityRepository
import com.buddy.domain.region.repository.RegionProvinceRepository
import com.univocity.parsers.csv.CsvParser
import com.univocity.parsers.csv.CsvParserSettings
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.support.TransactionTemplate
import java.nio.charset.Charset

@Configuration
class RegionSeedRunner(
    private val regionProvinceRepository: RegionProvinceRepository,
    private val regionMunicipalityRepository: RegionMunicipalityRepository,
    private val em: EntityManager,
    private val tx: TransactionTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun seedRegions() = ApplicationRunner {

        if (regionProvinceRepository.count() > 0L || regionMunicipalityRepository.count() > 0L) {
            log.info("Region seed skipped (existing data).")
            return@ApplicationRunner
        }

        val provRows = parseNoHeader("seed/1.csv") // [code2, name]
        val muniRows = parseNoHeader("seed/2.csv") // [code5, code2, name, full_name]

        // 1) 시·도: 배치 단위로 각각 '하나의 트랜잭션'으로 커밋
        provRows.chunked(1000).forEach { batch ->
            tx.executeWithoutResult {
                var i = 0
                for (r in batch) {
                    if (r.size < 2) continue
                    val code2 = r[0].trim().padStart(2, '0')
                    val name  = r[1].trim()
                    if (!regionProvinceRepository.existsByOfficialCode(code2)) {
                        regionProvinceRepository.save(RegionProvince(officialCode = code2, name = name))
                    }
                    if (++i % 300 == 0) { em.flush(); em.clear() }
                }
            }
        }
        log.info("Provinces seeded: {}", regionProvinceRepository.count())

        // 상위 코드 맵
        val provByCode = regionProvinceRepository.findAll().associateBy { it.officialCode }

        // 2) 시/군/구
        muniRows.chunked(1000).forEach { batch ->
            tx.executeWithoutResult {
                var i = 0
                for (r in batch) {
                    if (r.size < 3) continue
                    val code5 = r[0].trim().padStart(5, '0')
                    val code2 = r[1].trim().padStart(2, '0')
                    val name  = r[2].trim()

                    // 정합성
                    if (code5.length != 5 || code2.length != 2 || !code5.startsWith(code2)) {
                        log.warn("Skip invalid muni row: code5={}, code2={}, name={}", code5, code2, name)
                        continue
                    }
                    val province = provByCode[code2]
                    if (province == null) {
                        log.warn("Skip muni row due to missing province {}: muni={} {}", code2, code5, name)
                        continue
                    }

                    if (!regionMunicipalityRepository.existsByOfficialCode(code5)) {
                        regionMunicipalityRepository.save(RegionMunicipality(officialCode = code5, province = province, name = name))
                    }
                    if (++i % 500 == 0) { em.flush(); em.clear() }
                }
            }
        }
        log.info("Municipalities seeded: {}", regionMunicipalityRepository.count())
    }

    private fun parseNoHeader(path: String): List<Array<String>> {
        val res = ClassPathResource(path)
        require(res.exists()) { "CSV not found: $path" }
        val set = CsvParserSettings().apply {
            isHeaderExtractionEnabled = false
            skipEmptyLines = true
            ignoreLeadingWhitespaces = true
            ignoreTrailingWhitespaces = true
            format.delimiter = ','
            format.quote = '"'
        }
        val parser = CsvParser(set)
        fun read(cs: Charset) =
            parser.parseAll(res.inputStream.reader(cs)).map { row -> row.map { it?.trim() ?: "" }.toTypedArray() }

        return read(Charsets.UTF_8)
    }
}