package foo.bar.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import foo.bar.domain.BasicVO;
import foo.bar.exceptions.ExampleQueryException;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.utils.HqlConditions;

public interface Service<VO extends BasicVO<?>> {

	public int countAll() throws InstantiationException, IllegalAccessException, ExampleQueryException;

	public List<VO> findAll() throws InstantiationException, IllegalAccessException, ExampleQueryException;

	public VO findByPk(Object primaryKey);

	public VO findCustomByPk(Object primaryKey, String[] fields);

	public int countByExample(VO example, Map<String, HqlConditions> filter) throws ExampleQueryException, InstantiationException;

	public List<VO> findByExample(VO example, Map<String, HqlConditions> filter) throws ExampleQueryException, InstantiationException;

	public List<VO> findCustomByExample(VO example, String[] fields, Map<String, HqlConditions> filter)
			throws ExampleQueryException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException;

	boolean delete(VO element);

	public VO save(VO element) throws UniqueException;

	public VO update(VO element);

	public List<VO> saveList(List<VO> list) throws UniqueException;

	public List<VO> updateList(List<VO> list) throws UniqueException;
	
	public boolean deleteList(List<VO> list);

}
