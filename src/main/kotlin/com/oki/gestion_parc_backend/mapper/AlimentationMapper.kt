import com.oki.gestion_parc_backend.dto.AlimentationDto
import com.oki.gestion_parc_backend.mapper.DepenseMapper.formatter
import com.oki.gestion_parc_backend.mapper.IngredientAlimentationMapper
import com.oki.gestion_parc_backend.model.Alimentation


object AlimentationMapper {
    fun toDto(entity: Alimentation) = AlimentationDto(
        id = entity.id,
        date = entity.date,
        mode = entity.mode,
        ingredients = entity.ingredients.map { IngredientAlimentationMapper.toDto(it) },
        codeAnimal = entity.animal?.codeAnimal,
        typeAnimalId = entity.typeAnimal?.id,
        fournisseurId = entity.fournisseur?.id,
        coutTotal = entity.coutTotal,
        sourceReference = entity.sourceReference
    )
}
