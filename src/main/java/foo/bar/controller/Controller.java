package foo.bar.controller;

import java.util.List;
import java.util.Map;

import foo.bar.service.utils.HqlConditions;

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
