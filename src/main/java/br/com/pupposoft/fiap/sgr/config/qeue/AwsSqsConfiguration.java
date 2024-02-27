package br.com.pupposoft.fiap.sgr.config.qeue;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;

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

	@Value("${cloud.sqs.status-pedido.endpoint}")
	private String endpointStatusPedido;
	
	@Value("${cloud.sqs.notificar-cliente.endpoint}")
	private String endpointNotificarCliente;

	@NonNull
	private Environment environment;

	
	@Bean
    public JmsTemplate statusPedidoTemplate() {
        return new JmsTemplate(createSQSConnectionFactoryStatusPedido());
    }
	
	@Bean
	public JmsTemplate notifyClienteTemplate() {
		return new JmsTemplate(createSQSConnectionFactoryNotifyClient());
	}

	private SQSConnectionFactory createSQSConnectionFactoryStatusPedido() {

		final SqsClient sqsClient;

		String awsProfile = environment.getProperty("awsProfile");
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
	
	private SQSConnectionFactory createSQSConnectionFactoryNotifyClient() {
		
		final SqsClient sqsClient;
		
		String awsProfile = environment.getProperty("awsProfile");
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
