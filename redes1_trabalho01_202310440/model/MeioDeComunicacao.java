package model;

import controller.ControlerTelaPrincipal;
import util.ManipulacaoBits;

/**
 * classe responsavel por simular a funcao do meio de comunicacao
 * pega a mensagem codificada e "envia" para o Receptor
 */
public class MeioDeComunicacao {

  /**
   * este metodo simula a transmissao da informacao no meio de
   * comunicacao, passando de um pontoA (transmissor) para um
   * ponto B (receptor)
   * 
   * @param fluxoBrutoDeBits // a simulacao do sinal que sera enviado
   */
  public MeioDeComunicacao(int fluxoBrutoDeBits[]) {

    int fluxoBrutoDeBitsPontoA[] = fluxoBrutoDeBits; // como o fluxo sai do ponto A entao o fluxo bruto de bits eh
                                                     // atribuido ao fluxoBruto no ponto A
    int fluxoBrutoDeBitsPontoB[] = new int[fluxoBrutoDeBitsPontoA.length]; // o fluxo b tem que ter o mesmo tamanho do A
                                                                           // uma vez que ele recebera todo o sinal no
                                                                           // ponto B
    // simulacao dde transferencia de dados
    for (int i = 0; i < fluxoBrutoDeBitsPontoA.length; i++) {

      fluxoBrutoDeBitsPontoB[i] = fluxoBrutoDeBitsPontoA[i]; // transfere cada um dos inteiros de A para B na mesma posicao

    }

    // parte referenta a animacao somente
    int[] fluxoParaAnimacao = ManipulacaoBits.desempacotarBits(fluxoBrutoDeBitsPontoA,
        (ControlerTelaPrincipal.controlerTelaPrincipal.getCaixaTextoTransmitido().length() * 8)); // Cria um fluxo
                                                                                                  // simples equivalente
                                                                                                  // ao codificado para
                                                                                                  // criar a ANIMACAO
    // de maneira consistente

    ControlerTelaPrincipal.controlerTelaPrincipal.desenharSinalTransmissao(fluxoParaAnimacao);

    // chama proxima camada
    new CamadaFisicaReceptora(fluxoBrutoDeBitsPontoB);

  }// fim do metodo MeioDeTransmissao

} // fim da classe
