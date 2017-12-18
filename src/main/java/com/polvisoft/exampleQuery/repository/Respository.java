package com.polvisoft.exampleQuery.repository;

import java.util.List;

import com.polvisoft.exampleQuery.domain.BasicDTO;
import com.polvisoft.exampleQuery.exceptions.ExampleQueryException;
import com.polvisoft.exampleQuery.exceptions.UniqueException;
import com.polvisoft.exampleQuery.filter.FilterMap;

public interface Respository<DTO extends BasicDTO<?>> {

	public int countAll() throws ExampleQueryException;

	public List<DTO> findAll() throws ExampleQueryException;

	public DTO findByPk(Object primaryKey);

	public DTO findCustomByPk(Object primaryKey, String[] fields) throws ExampleQueryException;

	public int countByExample(DTO example, FilterMap filter) throws ExampleQueryException;

	public List<DTO> findByExample(DTO example, FilterMap filter) throws ExampleQueryException;

	public List<DTO> findByExample(DTO example, FilterMap filter, int pageNumber, int pageSize)
			throws ExampleQueryException;

	public List<DTO> findCustomByExample(DTO example, String[] fields, FilterMap filter) throws ExampleQueryException;

	public List<DTO> findCustomByExample(DTO example, String[] fields, FilterMap filter, int pageNumber, int pageSize)
			throws ExampleQueryException;

	boolean delete(DTO element);

	public DTO save(DTO element) throws UniqueException;

	public DTO update(DTO element) throws UniqueException;

	public List<DTO> saveList(List<DTO> list) throws UniqueException;

	public List<DTO> updateList(List<DTO> list) throws UniqueException;

	public boolean deleteList(List<DTO> list);

}
