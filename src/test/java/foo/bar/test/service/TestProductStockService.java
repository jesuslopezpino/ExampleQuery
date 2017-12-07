package foo.bar.test.service;

import java.util.HashMap;
import java.util.Map;

import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ProductStockServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.test.TestCommon;
import foo.bar.test.given.GivenProduct;
import foo.bar.test.given.GivenProductStock;

public class TestProductStockService extends TestCommon<ProductStockServiceImpl, ProductStock, GivenProductStock> {

}