package br.com.pupposoft.fiap.sgr.config.qeue;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;

import jakarta.jms.Session;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@EnableJms
@RequiredArgsConstructor
public class AwsSqsConfiguration {

	private static final String AWS_PROFILE = "awsProfile";

	@Value("${cloud.sqs.status-pedido.endpoint}")
	private String endpointStatusPedido;
	
	@Value("${cloud.sqs.notificar-cliente.endpoint}")
	private String endpointNotificarCliente;
	
	@Value("${cloud.sqs.efetuar-pagamento.endpoint}")
	private String endpointEfetuarPagamento;

	@Value("${cloud.sqs.concurrency}")
	private String concurrency;
	
	@NonNull
	private Environment environment;

	
	@Bean
    public JmsTemplate statusPedidoTemplate() {
        return new JmsTemplate(createSQSConnectionFactoryStatusPedido());
    }
	
	@Bean
	public JmsTemplate notifyTemplate() {
		return new JmsTemplate(createSQSConnectionFactoryNotifyClient());
	}

	@Bean
	public DefaultJmsListenerContainerFactory efetuarPagamentoSqsFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(createSQSConnectionFactoryEfetuarPagamento());
		factory.setDestinationResolver(new DynamicDestinationResolver());
		factory.setConcurrency(concurrency);
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		return factory;
	}
	
	
	
	private SQSConnectionFactory createSQSConnectionFactoryStatusPedido() {

		final SqsClient sqsClient;

		String awsProfile = environment.getProperty(AWS_PROFILE);
		if(awsProfile == null) {
			sqsClient = SqsClient.builder()
					.region(Region.US_WEST_2)
					.endpointOverride(URI.create(endpointStatusPedido))
					.credentialsProvider(EnvironmentVariableCredentialsProvider.create())//AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY and AWS_SESSION_TOKEN environment variables
					.build();
		} else {
			ProfileCredentialsProvider awsProvider = ProfileCredentialsProvider.create(awsProfile);
			sqsClient = SqsClient.builder()
					.region(Region.US_WEST_2)
					.endpointOverride(URI.create(endpointStatusPedido))
					.credentialsProvider(awsProvider)
					.build();
		}


		return new SQSConnectionFactory(new ProviderConfiguration(), sqsClient);
	}
	
	private SQSConnectionFactory createSQSConnectionFactoryEfetuarPagamento() {
		
		final SqsClient sqsClient;
		
		String awsProfile = environment.getProperty(AWS_PROFILE);
		if(awsProfile == null) {
			sqsClient = SqsClient.builder()
					.region(Region.US_WEST_2)
					.endpointOverride(URI.create(endpointEfetuarPagamento))
					.credentialsProvider(EnvironmentVariableCredentialsProvider.create())//AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY and AWS_SESSION_TOKEN environment variables
					.build();
		} else {
			ProfileCredentialsProvider awsProvider = ProfileCredentialsProvider.create(awsProfile);
			sqsClient = SqsClient.builder()
					.region(Region.US_WEST_2)
					.endpointOverride(URI.create(endpointEfetuarPagamento))
					.credentialsProvider(awsProvider)
					.build();
		}
		
		
		return new SQSConnectionFactory(new ProviderConfiguration(), sqsClient);
	}
	
	private SQSConnectionFactory createSQSConnectionFactoryNotifyClient() {
		
		final SqsClient sqsClient;
		
		String awsProfile = environment.getProperty(AWS_PROFILE);
		if(awsProfile == null) {
			sqsClient = SqsClient.builder()
					.region(Region.US_WEST_2)
					.endpointOverride(URI.create(endpointNotificarCliente))
					.credentialsProvider(EnvironmentVariableCredentialsProvider.create())//AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY and AWS_SESSION_TOKEN environment variables
					.build();
		} else {
			ProfileCredentialsProvider awsProvider = ProfileCredentialsProvider.create(awsProfile);
			sqsClient = SqsClient.builder()
					.region(Region.US_WEST_2)
					.endpointOverride(URI.create(endpointNotificarCliente))
					.credentialsProvider(awsProvider)
					.build();
		}
		
		
		return new SQSConnectionFactory(new ProviderConfiguration(), sqsClient);
	}
	

	
}
