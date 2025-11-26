import com.oki.gestion_parc_backend.dto.AlimentationDto
import com.oki.gestion_parc_backend.mapper.DepenseMapper.formatter
import com.oki.gestion_parc_backend.model.Alimentation
object AlimentationMapper {

    fun toDto(entity: Alimentation) =
        AlimentationDto(
            id = entity.id,
            date=entity.date,
            typeAlimentId = entity.typeAliment.id,
            quantiteKg = entity.quantiteKg,
            prixUnitaire = entity.prixUnitaire,
            codeAnimal = entity.animal?.codeAnimal,
            typeAnimalId = entity.typeAnimal?.id,
            typeAlimentLibelle = entity.typeAliment.libelle,
            fournisseurId = entity.fournisseur?.id,
            coutTotal = entity.coutTotal
        )
}