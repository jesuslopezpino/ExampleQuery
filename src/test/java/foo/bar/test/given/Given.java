package foo.bar.test.given;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import javax.persistence.EntityManager;

import foo.bar.domain.BasicVO;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ServiceImpl;
import foo.bar.service.utils.HqlConditions;

public abstract class Given<VO extends BasicVO<?>, ServiceVO extends ServiceImpl<VO>> {

	protected EntityManager entityManager;

	protected Class serviceVoClass;

	protected ServiceVO service;

	public abstract void givenExamplesEnviroment()
			throws UniqueException, InstantiationException, IllegalAccessException;

	public abstract String[] initCustomFields();

	public abstract String initUpdateField();

	public abstract VO[] initExamples() throws UniqueException, InstantiationException, IllegalAccessException;

	public abstract VO initSaveEntity() throws UniqueException, InstantiationException, IllegalAccessException;

	public abstract Map<String, HqlConditions> initFilter();

	public abstract Map<String, Object> initEntityFields();

	public abstract Object initUpdateValue();

	public Given(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
		this.entityManager = entityManager;
		this.serviceVoClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
		this.service = (ServiceVO) this.serviceVoClass.newInstance();
		this.service.setEntityManager(entityManager);
	}
}
