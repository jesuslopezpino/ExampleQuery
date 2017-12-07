package foo.bar.test.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foo.bar.domain.Product;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ProductServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.test.TestCommon;
import foo.bar.test.given.GivenProduct;

public class TestProductService extends TestCommon<ProductServiceImpl, Product, GivenProduct> {

	
}