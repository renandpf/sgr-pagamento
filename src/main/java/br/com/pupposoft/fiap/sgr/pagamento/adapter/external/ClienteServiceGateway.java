package br.com.pupposoft.fiap.sgr.pagamento.adapter.external;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json.ClienteJson;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.ClienteDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToAccessPedidoServiceException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.ClienteGateway;
import br.com.pupposoft.fiap.starter.http.HttpConnectGateway;
import br.com.pupposoft.fiap.starter.http.dto.HttpConnectDto;
import br.com.pupposoft.fiap.starter.http.exception.HttpConnectorException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteServiceGateway implements ClienteGateway {

	@Value("${sgr.cliente-service.url}")
	private String baseUrl;
	
	@NonNull
	private HttpConnectGateway httpConnectGateway;

	@NonNull
    private ObjectMapper mapper;
	
	@Override
	public Optional<ClienteDto> obterPorId(Long clienteId) {
		try {
			return process(clienteId);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorToAccessPedidoServiceException();
		}
	}

	private Optional<ClienteDto> process(Long clienteId) throws Exception {
		try {
			final String url = baseUrl + "/sgr/gerencial/clientes/" + clienteId;
			
			HttpConnectDto httpConnectDto = HttpConnectDto.builder().url(url).build();
			
			final String response = httpConnectGateway.get(httpConnectDto);
			log.info("response={}", response);
			
			ClienteJson pedidoJson = mapper.readValue(response, ClienteJson.class);
			ClienteDto clienteDto = mapJsonToDto(pedidoJson);
			
			return Optional.of(clienteDto);

			
		} catch (HttpConnectorException e) {
			if(e.getHttpStatus() == 404) {
				log.warn("Pedido not found. pedidoId={}", clienteId);
				return Optional.empty();
			} else {
				throw e;
			}
		}
	}

	private ClienteDto mapJsonToDto(ClienteJson pedidoJson) {
		return ClienteDto.builder()
				.id(pedidoJson.getId())
				.nome(pedidoJson.getNome())
				.cpf(pedidoJson.getCpf())
				.email(pedidoJson.getEmail())
				.build();
	}

}
