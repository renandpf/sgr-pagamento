CREATE TABLE `Pagamento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `identificadorPagamentoExterno` varchar(255) NOT NULL,
  `pedidoId` int DEFAULT NULL,
  `valor` decimal(10,0) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `PlataformaPagamento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL,
  `nome` varchar(100) DEFAULT NULL,
  `status` int NOT NULL,
  PRIMARY KEY (`id`)
);
