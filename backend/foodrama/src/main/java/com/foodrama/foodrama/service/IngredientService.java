package com.foodrama.foodrama.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.foodrama.foodrama.model.Ingredient;
import com.foodrama.foodrama.model.dto.IngredientDto;
import com.foodrama.foodrama.repository.IngredientRepository;

import jakarta.transaction.Transactional;

@Service
public class IngredientService {

	@Autowired
	private IngredientRepository ingredientRepository;
	
	/**
	 * Returns a list of all available ingredients in the database ordered alphabetically.
	 *
	 * @return list of ingredients sorted by name ascending
	 */
	@Transactional
	public List<IngredientDto> getAll() {
		List<IngredientDto> ingredients = ingredientRepository.findAll()
				.stream()
				.sorted()
				.map(IngredientDto::new)
				.collect(Collectors.toList());
		
		if(ingredients.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No ingredients found");
		}
		
		return ingredients;
	}
	
	/**
	 * Returns the ingredient with passed id.
	 *
	 * @param id id of the ingredient
	 * @return the ingredient with the matching id requested
	 */
	@Transactional
	public IngredientDto getById(Long id) {
		return ingredientRepository.findById(id)
				.map(IngredientDto::new)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found with id: " + id));
	}
	
	/**
	 * Returns the ingredient with passed barCode.
	 *
	 * @param id id of the ingredient
	 * @return the ingredient with the matching id requested
	 */
	@Transactional
	public IngredientDto getByBarCode(String barCode) {
		return ingredientRepository.findByBarCode(barCode)
				.map(IngredientDto::new)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found with barCode: " + barCode));
	}
	
	/**
     * Save a new ingredient to the database.
     *
     * @param ingredientDto the DTO containing information about the ingredient
     * @return the saved ingredient as a DTO
     */
	@Transactional
    public IngredientDto save(IngredientDto ingredientDto) {
    	try {
    		return new IngredientDto(ingredientRepository.save(ingredientDto.toEntity()));
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR , "Error saving ingredient: " + e.getMessage());
		}
    }
    
    /**
     * Edit a ingredient in the database.
     *
     * @param id id of the ingredient
     * @param ingredientDto the DTO containing information about the ingredient
     * @return the saved ingredient as a DTO
     */
	@Transactional
    public IngredientDto edit(Long id, IngredientDto ingredientDto) {
		Ingredient ingredient = ingredientRepository.findById(id)
        		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found with id: " + id));
    	
		try {
//	    	ingredient.setBarCode(ingredientDto.barCode());
			ingredient.setName(ingredientDto.name());
			ingredient.setPackageQuantity(ingredientDto.packageQuantity());
			ingredient.setPrice(ingredientDto.price());
			ingredient.setQuantityUnit(ingredientDto.quantityUnit().getLabel());
	    	
	        return new IngredientDto(ingredientRepository.save(ingredient));
    	} catch (Exception e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR , "Error editing ingredient: " + e.getMessage());
    	}
    }

    /**
     * Delete the ingredient with the specified ID from the database.
     *
     * @param id the ID of the ingredient to delete
     */
	@Transactional
    public void delete(Long id) {
		ingredientRepository.findById(id)
        		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found with id: " + id));
    	
		try {
    		ingredientRepository.deleteById(id);
    	} catch (Exception e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR , "Error deleting ingredient: " + e.getMessage());
    	}
    }
}
