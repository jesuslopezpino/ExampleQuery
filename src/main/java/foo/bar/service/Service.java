package foo.bar.service;

import java.util.List;
import java.util.Map;

import foo.bar.exceptions.ExampleQueryException;
import foo.bar.exceptions.UniqueException;

public interface Service<VO> {

	VO findByPk(Object primaryKey);

	VO findCustomByPk(Object primaryKey, String[] fields);
	
	List<VO> findByExample(VO example, Map<String, String> filter) throws ExampleQueryException;

	public List<VO> findCustomByExample(VO example, String[] fields, Map<String, String> filter) throws ExampleQueryException;
	
	boolean delete(VO element);

	VO save(VO element) throws UniqueException;

	VO update(VO element);

}
