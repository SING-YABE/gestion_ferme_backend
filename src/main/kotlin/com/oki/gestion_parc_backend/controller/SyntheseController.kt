package com.oki.gestion_parc_backend.controller
import com.oki.gestion_parc_backend.dto.SyntheseFinanciereDTO
import com.oki.gestion_parc_backend.service.SyntheseService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeParseException

@RestController
@RequestMapping("/api/synthese")
@CrossOrigin(origins = ["*"])
class SyntheseController(
    private val syntheseService: SyntheseService
) {


    @GetMapping
    fun getSynthese(
        @RequestParam(required = false) start: String?,
        @RequestParam(required = false) end: String?
    ): SyntheseFinanciereDTO {
        return if (!start.isNullOrBlank() && !end.isNullOrBlank()) {
            try {
                val startDate = LocalDate.parse(start)
                val endDate = LocalDate.parse(end)
                syntheseService.getSyntheseBetween(startDate, endDate)
            } catch (ex: DateTimeParseException) {
                throw IllegalArgumentException(" start et end auformat yyyy-MM-dd")
            }
        } else {
            syntheseService.getSynthese()
        }
    }
}
