package foo.bar.test.given;

import foo.bar.domain.BasicVO;
import foo.bar.service.impl.ServiceImpl;

public abstract class Given<VO extends BasicVO<?>, ServiveVO extends ServiceImpl<VO>> {

	public abstract void givenExamplesEnviroment();
	
}
