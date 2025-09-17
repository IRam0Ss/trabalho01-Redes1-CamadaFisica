package model;

import controller.ControlerTelaPrincipal;

/**
 * camada responsavel por simular a camada fisica do sistema receptor recebe a
 * mensagem vinda codificada na forma de sinal do meio de comulicacao e
 * descodifica ela
 */
public class CamadaFisicaReceptora {

  /**
   * construtor da classe responsavel por receber a mensagem codificada e
   * decodificasr de acordo com o metodo que foi selecionado
   * 
   * @param quadro a mensagem codificada
   */
  public CamadaFisicaReceptora(int[] quadro) {

    ControlerTelaPrincipal.controlerTelaPrincipal.exibirRepresentSinalRecebido(quadro); // exibe a representacao do
                                                                                        // sinal recebido

    int tipoDeDecodificacao = ControlerTelaPrincipal.controlerTelaPrincipal.opcaoSelecionada();
    int fluxoBrutoDeBits[] = null;
    switch (tipoDeDecodificacao) {
      case 0: // codificao binaria
        fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoBinaria(quadro);
        break;
      case 1: // codificacao manchester
        fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoManchester(quadro);
        break;
      case 2: // codificacao manchester diferencial
        fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(quadro);
        break;
    }// fim do switch/case

    // chama proxima camada

    new CamadaAplicacaoReceptora(fluxoBrutoDeBits);
  }// fim do metodo CamadaFisicaTransmissora

  /**
   * metodo para decodificar o Binario, basicamente retorna o mesmo array
   * 
   * @param quadro conjunto de bits recebido
   * @return o conjunto de bits decodificado
   */
  public int[] CamadaFisicaReceptoraDecodificacaoBinaria(int[] quadro) {
    return quadro;
  } // fim do metodo

  /**
   * decodifica o codigo manchester
   * 10 -> 1
   * 01 -> 0
   * 
   * @param quadro array com bits codificados em manchester
   * @return array decodificado
   */
  public int[] CamadaFisicaReceptoraDecodificacaoManchester(int[] quadro) {

    int totalBitsManchester = (quadro.length * 32); // descobre quantos bits tem a mensagem recebida
    int totalBitsOriginal = totalBitsManchester / 2; // calcula quantos bits tem a mensagem original e por consequencia
                                                     // tera a mensagem decodificada
    int tamanhoArrayDecodificado = (totalBitsOriginal + 31) / 32;
    int[] mensagemDecodificada = new int[tamanhoArrayDecodificado];

    // itera os bits em pares, uma vez que a cada 2 bits do manchester tem 1 bit do
    // original
    for (int i = 0; i < totalBitsManchester; i += 2) {

      // le e recupera o bit1 do par
      int indiceBit1 = i / 32;
      int posicaoBit1 = 31 - (i % 32);
      int bit1 = (quadro[indiceBit1] >> posicaoBit1) & 1;

      // le e recupera o bit2 do par
      int indiceBit2 = (i + 1) / 32;
      int posicaoBit2 = 31 - ((i + 1) % 32);
      int bit2 = (quadro[indiceBit2] >> posicaoBit2) & 1;

      // determina qual o bit original
      int bitOriginal = 0;
      if (bit1 == 1 && bit2 == 0) { // se o par de bit for 10 entao o bit original eh 1, caso contrario ele eh 0
        bitOriginal = 1;
      }

      // escreve o bit no array de retorno
      if (bitOriginal == 1) {
        int posicaoGlobalOriginal = i / 2; // mapea a posicao do fluxo manchester de volta na posicao original
        int indiceOriginal = posicaoGlobalOriginal / 32;
        int posicaoOriginal = 31 - (posicaoGlobalOriginal % 32);
        mensagemDecodificada[indiceOriginal] = mensagemDecodificada[indiceOriginal] | (1 << posicaoOriginal);
      }

    }
    return mensagemDecodificada;
  }// fim do metodo

  /**
   * decodifica o manchester diferencial
   * primeiro bit eh o padrao, ja os proximos bits sao traduzidos a partir da
   * existencia de transicao e a nao existencia
   * sem transicao -> 1
   * com transicao -> 0
   * 
   * @param quadro o bacote de bits codificado em manchester diferencial
   * @return o pacote de inteiros decodificado, com a informacao da mensagem em
   *         binario
   */
  public int[] CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(int[] quadro) {

    int totalBitsDiferencial = (quadro.length * 32); // descobre quantos bits tem a mensagem recebida
    int totalBitsOriginal = totalBitsDiferencial / 2; // calcula quantos bits tem a mensagem original e por consequencia
                                                      // tera a mensagem decodificada
    int tamanhoArrayDecodificado = (totalBitsOriginal + 31) / 32;
    int[] mensagemDecodificada = new int[tamanhoArrayDecodificado];

    int nivelAnterior = 1; // variavel padronizada para iniciar a leitura, TEM que ser igual ao
                           // "nivelAtual" do transmissor

    // percorre os bits codificados em pares
    for (int i = 0; i < totalBitsDiferencial; i += 2) {

      // leitura da primeira parte do sinal
      int posicaoGeralSinal1 = i;
      int indiceSinal1 = posicaoGeralSinal1 / 32;
      int posicaoSinal1 = 31 - (posicaoGeralSinal1 % 32);
      int primeiroNivelSinal = (quadro[indiceSinal1] >> posicaoSinal1) & 1; // le no array o sinal1 e o armazena

      // define o bit original comparando o nivel do sinal encontrado com o anterior
      int bitOriginal;
      if (primeiroNivelSinal == nivelAnterior) { // se o sinal se manteve, nao ouve transicao logo bit = 1
        bitOriginal = 1;
      } else { // caso contrario ouve transicao logo bit = 0
        bitOriginal = 0;
      }

      // escreve o bit decodificado no array de decodificacao
      if (bitOriginal == 1) {
        int posicaoGlobalOriginal = i / 2; // mapea a posicao do fluxo diferencil de volta na posicao original
        int indiceOriginal = posicaoGlobalOriginal / 32;
        int posicaoOriginal = 31 - (posicaoGlobalOriginal % 32);
        mensagemDecodificada[indiceOriginal] = mensagemDecodificada[indiceOriginal] | (1 << posicaoOriginal);
      }

      // atualiza o nivel anterior como a segunda parte do sinal, uma vez que eh a
      // aprtir dele que se sabe se ouvbe ou nao transicao
      int posicaoGeralSinal2 = i + 1;
      int indiceSinal2 = posicaoGeralSinal2 / 32;
      int posicaoSinal2 = 31 - (posicaoGeralSinal2 % 32);
      nivelAnterior = (quadro[indiceSinal2] >> posicaoSinal2) & 1; // le o sinal2 ou seja a segunda parte do sinal e
      // armazena no nivel anterior

    }

    return mensagemDecodificada;
  }// fim do metodo

}// fim da classe
