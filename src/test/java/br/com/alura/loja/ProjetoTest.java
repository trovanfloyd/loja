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

import br.com.alura.loja.modelo.Projeto;

public class ProjetoTest {
	
	
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
	public void testarProjeto() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		String conteudo = target.path("/projetos").request().get(String.class);
		Assert.assertTrue(conteudo.contains("Minha"));
	}*/
	
	@Test
	public void testaQueBuscarUmProjetoTrazOProjetoEsperado() {
		
		//Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		String conteudo = target.path("/projetos/1").request().get(String.class);
		Projeto projeto = (Projeto) new XStream().fromXML(conteudo);
		Assert.assertEquals("Minha loja", projeto.getNome());
	}
	
	@Test
	public void testaAdicionarNovoProjeto() {
		
		//Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhot:8080");
		
		Projeto projeto = new Projeto("Novo Projeto", 2016);
		
		String xml = projeto.toXML();
		
		Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
		
		Response response = target.path("/projetos").request().post(entity);
		
		Assert.assertEquals(201, response.getStatus());
		
		String location = response.getHeaderString("Location");
		
		String conteudo = client.target(location).request().get(String.class);
		
		Assert.assertTrue(conteudo.contains("Novo Projeto"));
	}
}
