package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

public class ClientTest {
	
	HttpServer server;
	Client client;
	
	@Before
	public void startServidor() {
		server = Servidor.startaServidor();
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		this.client = ClientBuilder.newClient(config);
	}
	
	@After
	public void stopServidor() {
		server.stop();
	}

	/*@Test
	public void testaQueAConexaoComOServidorFunciona() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://www.mocky.io");
		String content = target.path("/v2/52aaf5deee7ba8c70329fb7d").request().get(String.class);
		Assert.assertTrue(content.contains("<rua>Rua Vergueiro 3185"));
	}*/
	
	@Test
	public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
		//Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		Carrinho content = target.path("/carrinhos/1").request().get(Carrinho.class);
//		Carrinho carrinho = (Carrinho) new XStream().fromXML(content);
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", content.getRua());
	}
	
	@Test
	public void testaAdicionarCarrinho() {
		//Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		
		Carrinho carrinho = new Carrinho();
        carrinho.adiciona(new Produto(314L, "Tablet", 999, 1));
        carrinho.setRua("Rua Vergueiro");
        carrinho.setCidade("Sao Paulo");
        String xml = carrinho.toXml();
        
        Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);

        Response response = target.path("/carrinhos").request().post(entity);
        Assert.assertEquals(201, response.getStatus());
        String location = response.getHeaderString("Location");
        
        String conteudo = client.target(location).request().get(String.class);
        Assert.assertTrue(conteudo.contains("Tablet"));
        
//        Assert.assertEquals("<status>sucesso</status>", response.readEntity(String.class));
	}
	
	@Test
	public void removerProdutoCarrinho() {
		
		//Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		
		Response response = target.path("/carrinhos/1/produtos/6237").request().delete();
		
		Assert.assertEquals(200, response.getStatus());
	}
	
	@Test
	public void alterarProdutoCarrinho() {
		
		//Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		
		Produto produto = new Produto(6237, "Produto novo", 50, 2);
		String xml = new XStream().toXML(produto);
		Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
		
		Response response = target.path("/carrinhos/1/produtos/6237").request().put(entity);
		
		Assert.assertEquals(200, response.getStatus());
		
		String content = target.path("/carrinhos/1").request().get(String.class);
		Carrinho carrinho = (Carrinho) new XStream().fromXML(content);
		System.out.println(carrinho.getProdutos().get(1).getNome());
		/*
		carrinho.getProdutos().forEach(p ->  {
			if (p.getId() == 6237) {
				 Assert.assertTrue(p.getNome().contains("Produto novo"));
			}
		});*/
		
		
	}
}
