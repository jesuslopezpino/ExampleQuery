package com.polvisoft.service;

import java.util.List;

import com.polvisoft.domain.BasicVO;
import com.polvisoft.exceptions.ExampleQueryException;
import com.polvisoft.exceptions.UniqueException;
import com.polvisoft.filter.FilterMap;

public interface Service<VO extends BasicVO<?>> {

	public int countAll() throws ExampleQueryException;

	public List<VO> findAll() throws ExampleQueryException;

	public VO findByPk(Object primaryKey);

	public VO findCustomByPk(Object primaryKey, String[] fields) throws ExampleQueryException;

	public int countByExample(VO example, FilterMap filter) throws ExampleQueryException;

	public List<VO> findByExample(VO example, FilterMap filter) throws ExampleQueryException;

	public List<VO> findByExample(VO example, FilterMap filter, int pageNumber, int pageSize)
			throws ExampleQueryException;

	public List<VO> findCustomByExample(VO example, String[] fields, FilterMap filter) throws ExampleQueryException;

	public List<VO> findCustomByExample(VO example, String[] fields, FilterMap filter, int pageNumber, int pageSize)
			throws ExampleQueryException;

	boolean delete(VO element);

	public VO save(VO element) throws UniqueException;

	public VO update(VO element) throws UniqueException;

	public List<VO> saveList(List<VO> list) throws UniqueException;

	public List<VO> updateList(List<VO> list) throws UniqueException;

	public boolean deleteList(List<VO> list);

}
