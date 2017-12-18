package com.polvisoft.exampleQuery.controller;

import java.util.List;
import java.util.Map;

import com.polvisoft.exampleQuery.enums.HqlConditions;

public interface Controller<DTO> {

	Map<String, HqlConditions> getFinderConditions();

	String goToFinder();
	
	void populateFinderForm();

	void resetFinderForm();

	String goToEditForm();
	
	void populateEditForm();

	void resetEditForm();

	List<DTO> findAll();

	List<DTO> findByExample(DTO example);

}
