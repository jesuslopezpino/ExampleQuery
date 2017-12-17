package com.polvisoft.exampleQuery.controller;

import java.util.List;
import java.util.Map;

import com.polvisoft.exampleQuery.service.utils.HqlConditions;

public interface Controller<VO> {

	Map<String, HqlConditions> getFinderConditions();

	String goToFinder();
	
	void populateFinderForm();

	void resetFinderForm();

	String goToEditForm();
	
	void populateEditForm();

	void resetEditForm();

	List<VO> findAll();

	List<VO> findByExample(VO example);

}
