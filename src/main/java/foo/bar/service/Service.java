package foo.bar.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import foo.bar.domain.BasicVO;
import foo.bar.exceptions.ExampleQueryException;
import foo.bar.exceptions.UniqueException;
import foo.bar.filter.FilterMap;

public interface Service<VO extends BasicVO<?>> {

	public int countAll() throws InstantiationException, IllegalAccessException, ExampleQueryException;

	public List<VO> findAll() throws InstantiationException, IllegalAccessException, ExampleQueryException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException;

	public VO findByPk(Object primaryKey);

	public VO findCustomByPk(Object primaryKey, String[] fields)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException;

	public int countByExample(VO example, FilterMap filter)
			throws ExampleQueryException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException;

	public List<VO> findByExample(VO example, FilterMap filter)
			throws ExampleQueryException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException;

	public List<VO> findCustomByExample(VO example, String[] fields, FilterMap filter)
			throws ExampleQueryException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException;

	boolean delete(VO element);

	public VO save(VO element) throws UniqueException;

	public VO update(VO element) throws UniqueException;

	public List<VO> saveList(List<VO> list) throws UniqueException;

	public List<VO> updateList(List<VO> list) throws UniqueException;

	public boolean deleteList(List<VO> list);

}
