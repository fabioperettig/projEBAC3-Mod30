package br.com.fabioperettig;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ClienteServiceTest.class, ClienteDAOTest.class,
    ProdutoServiceTest.class, ProdutoDAOTest.class, VendaDAOTest.class, EstoqueDAOTest.class})
public class AllTests { }
