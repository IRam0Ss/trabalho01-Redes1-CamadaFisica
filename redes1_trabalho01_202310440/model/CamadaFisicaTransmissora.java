package model;

import controller.ControlerTelaPrincipal;

/**
 * classe responsavel por simular a funcao da Camada Fissica Transmissora
 * pega a mensagem que veio da camada anterior (em binario) e codifica de acordo
 * com a opcao selecionada pelo usuario
 */
public class CamadaFisicaTransmissora {

  /**
   * trabalhando com o construtor da classe, essa classe eh responsavel por
   * aplicar a codificacao da mensaagem escolhida pelo usuario e enviar a
   * simulacoa de sinal para a proxima camada
   * 
   * @param quadro mensagem na forma binaria, o array de int, ja com os bits
   *               armazenados recebido da camada anterior
   */
  public CamadaFisicaTransmissora(int quadro[]) {
    int tipoDeCodificacao = ControlerTelaPrincipal.controlerTelaPrincipal.opcaoSelecionada(); // alterar de acordo o
                                                                                              // teste

    int fluxoBrutoDeBits[] = null; // eh a representacao do sinal que sera enviado
    switch (tipoDeCodificacao) {
      case 0: // codificao binaria
        fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoBinaria(quadro);
        break;
      case 1: // codificacao manchester
        fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoManchester(quadro);
        break;
      case 2: // codificacao manchester diferencial
        fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro);
        break;
    }// fim do switch/case

    ControlerTelaPrincipal.controlerTelaPrincipal.exibirRepresentSinalTransmitido(fluxoBrutoDeBits); // exibe o sinal
                                                                                                     // codificado que
                                                                                                     // sera transmitido

    new MeioDeComunicacao(fluxoBrutoDeBits); // envia o sinal a ser transmitido para a proxima "parte" o meio de
                                             // comunicacao
  } // fim do construtor

  /**
   * aplica a codificacao binaria na mensagem a ser transmitida
   * 
   * @param quadro traduzida em bits agrupados no array de
   *               inteiros, cada inteiro possui ate 32 bits (4
   *               char) da
   *               mensagem
   * @return como o sinal em binario eh uma traducao direta da mensagem em bits,
   *         retorna a propria
   */
  public int[] CamadaFisicaTransmissoraCodificacaoBinaria(int[] quadro) {
    return quadro;
  }// fim do metodo

  /**
   * aplica a codificacao de manchester na mensagem a ser transmitida
   * bit 1 -> 10
   * bit 0 -> 01
   * 
   * @param quadro string traduzida em bits agrupados no array de
   *               inteiros, cada inteiro possui ate 32 bits (4 char) da
   *               mensagem
   * @return retorna no array como sera o sinal transmitido em manchester
   */
  public int[] CamadaFisicaTransmissoraCodificacaoManchester(int[] quadro) {

    int totalBits = quadro.length * 32; // quantidade de bit armazenado no array de int
    int totalBitsManchester = totalBits * 2; // // quatidade de bits que tera o array pos codificacao
    int tamanhoArrayManchester = (totalBitsManchester + 31) / 32; // para garantir que arredonde pra cima em casos nao
                                                                  // exatos

    int[] pacoteManchester = new int[tamanhoArrayManchester]; // cria o array que vai ser devolvido

    // percorre cada bit do fluxo original e aplica codificacao manchester
    for (int i = 0; i < totalBits; i++) {

      // ler o bit original
      int indiceNoPacoteOriginal = i / 32;
      int posicaobitOriginal = 31 - (i % 32);

      int bitOriginal = (quadro[indiceNoPacoteOriginal] >> posicaobitOriginal) & 1;

      int sinal1, sinal2; // definir quais os sinais que o bit tera codificado
      if (bitOriginal == 1) {
        sinal1 = 1;
        sinal2 = 0;
        // 1 -> 10
      } else {
        sinal1 = 0;
        sinal2 = 1;
        // 0 ->01
      }

      // escreve o sinal 1 no pacoteManchester
      int posicaoSinal1 = i * 2;
      int indiceNoPacoteManchesterSinal1 = posicaoSinal1 / 32;
      int posicaoBitSinal1 = 31 - (posicaoSinal1 % 32);

      if (sinal1 == 1) {
        pacoteManchester[indiceNoPacoteManchesterSinal1] = pacoteManchester[indiceNoPacoteManchesterSinal1]
            | (1 << posicaoBitSinal1); // liga o bit no local certo
      }

      // escreve o sinal 2 no pacote manchester
      int posicaoSinal2 = (i * 2) + 1;
      int indiceNoPacoteManchesterSinal2 = posicaoSinal2 / 32;
      int posicaoBitSinal2 = 31 - (posicaoSinal2 % 32);

      if (sinal2 == 1) {
        pacoteManchester[indiceNoPacoteManchesterSinal2] = pacoteManchester[indiceNoPacoteManchesterSinal2]
            | (1 << posicaoBitSinal2);
      }

    }

    return pacoteManchester;
  }// fim do metodo

  /**
   * aplica a codificacao de manchester na mensagem a ser transmitida
   * 0 -> transicao
   * 1 -> sem transicao
   * 
   * @param quadro string traduzida em bits agrupados no array de
   *               inteiros, cada inteiro possui ate 32 bits (4 char) da
   *               mensagem
   * @return retorna no array como sera o sinal transmitido em
   *         manchesterdiferencial
   */
  public int[] CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(int[] quadro) {

    int totalBits = quadro.length * 32; // calcula o total de bit enviados pelo array
    int totalBitsDiferencial = totalBits * 2; // define qual o total dde bits final do manchester diferencial
    int tamanhoArrayDiferencial = (totalBitsDiferencial + 31) / 32; // define o tamanho necessario para o novo array
                                                                    // arredondando para cima
    int[] pacoteMancheterDiferencial = new int[tamanhoArrayDiferencial]; // cria o array

    int nivelAtual = 1; // variavel que controla o nivel atual do sinal, inicializacdo arbitrariamente

    // percorrer bit da mensagem original e aplicar manchester diferencial
    for (int i = 0; i < totalBits; i++) {

      // ler o bit original
      int indiceNoPacoteOriginal = i / 32;
      int posicaobitOriginal = 31 - (i % 32);

      int bitOriginal = (quadro[indiceNoPacoteOriginal] >> posicaobitOriginal) & 1;

      if (bitOriginal == 0) { // caso o bit seja 0 forca transicao invertendo o nivel do sinalatual
        nivelAtual = 1 - nivelAtual; // inverte (0 -> 1 e 1 -> 0)
      }
      // caso o bit original seja 1, mantem o mesmo sinal

      // escreve a primeira parte do sinal
      int posicaoSinal1 = i * 2;
      if (nivelAtual == 1) {
        int indicePacoteDiferencial = posicaoSinal1 / 32;
        int posicaoNovaDiferencial = 31 - (posicaoSinal1 % 32);
        pacoteMancheterDiferencial[indicePacoteDiferencial] = pacoteMancheterDiferencial[indicePacoteDiferencial]
            | (1 << posicaoNovaDiferencial);
      }

      nivelAtual = 1 - nivelAtual; // faz a inversao padrao para codificar o bit como um par de sinais

      // escreve a segunda parte do sinal
      int posicaoSinal2 = i * 2 + 1;
      if (nivelAtual == 1) {
        int indicePacoteDiferencial = posicaoSinal2 / 32;
        int posicaoNovaDiferencial = 31 - (posicaoSinal2 % 32);
        pacoteMancheterDiferencial[indicePacoteDiferencial] = pacoteMancheterDiferencial[indicePacoteDiferencial]
            | (1 << posicaoNovaDiferencial);
      }

    }

    return pacoteMancheterDiferencial;
  }// fim do metodo

}// fim da classe
