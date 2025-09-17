package util;

import controller.ControlerTelaPrincipal;

/**
 * Classe responsavel por todos os metodos relacionados a manipulacao de bits
 * que foram utilizados
 */
public class ManipulacaoBits {

  /**
   * converte uma String de qualquer tamanho em um array de inteiros (int[]),
   * onde cada inteiro armazena 32 bits da mensagem de forma agrupada.
   * 
   * @param mensagem A String a ser convertida.
   * @return Um array de int[] com os bits da mensagem agrupados.
   */
  public static int[] stringParaIntAgrupado(String mensagem) {

    char[] charMensagem = mensagem.toCharArray(); // converte a mensagem para o array de caracteres equivalentes
    int totalBits = (8 * charMensagem.length); // armazena o total de bits daquela mensagem

    // calcular quantos inteiros serao necessarios para armazenar a mensagem,
    // considerando que cada inteiro armazena 32 bits
    int tamanhoArray = (totalBits + 31) / 32; // para garantir que arredonde pra cima em casos nao exatos
    int[] pacoteBits = new int[tamanhoArray];

    int contadorBits = 0; // acompanhar os bits

    // loop por caractere da mensagem
    for (char caracter : charMensagem) {

      // loop pra cada bit do Char
      for (int i = 0; i < 8; i++) {

        int bit = (caracter >> (7 - i)) & 1; // 1 -> mascara usada (0000 0001);

        // Se o bit for 1, precisamos "liga-lo" na posicao correta
        if (bit == 1) {
          int indicePacote = contadorBits / 32; // calcula o indice que o bit sera armazenado
          int posicaoNoPacote = 31 - (contadorBits % 32); // calcula a posicao correta para nao sobrepor bits ja
                                                          // armazenados
          pacoteBits[indicePacote] = pacoteBits[indicePacote] | (1 << posicaoNoPacote); // armazena o bit no array
        }
        contadorBits++;
      }
    }
    return pacoteBits;
  } // fim metodo

  /**
   * Converte um array de inteiros (pacotes de 32 bits) de volta para uma String.
   * 
   * @param pacoteBits pacotesDeBits O array de int[] com os bits agrupados.
   * @param totalBits  totalDeBits O numero TOTAL de bits validos (a mensagem
   *                   original pode nao ocupar o ultimo int por completo)
   * @return A String resultante
   */
  public static String intAgrupadoParaString(int[] pacoteBits, int totalBits) {

    int totalChar = totalBits / 8; // calcula o total de caracteres uma vez que cada char sao 8 bits
    char[] charMensagem = new char[totalChar]; // cria o array de chars para mensagem

    // loop de reconstrucao dos char
    for (int i = 0; i < totalChar; i++) {
      int valorChar = 0; // inicializa o valor numerico do char como 0

      // ler os 8 bits de um char
      for (int j = 0; j < 8; j++) {
        int contadorBits = i * 8 + j;
        int indicePacote = contadorBits / 32;
        int posicaoNoPacote = 31 - (contadorBits % 32);

        // extrai bit do pacote
        int bit = (pacoteBits[indicePacote] >> posicaoNoPacote) & 1;

        if (bit == 1) { // caso o bit seja 1 adiciona o bit um no valor do char, para gerar o binario
                        // equivalente que sera convertido novamente para char
          valorChar = valorChar | (1 << (7 - j));
        }

      } // fim do loop por char
      if (valorChar != 0) { // caso o valor do char seja valido, adiciona o char no array da mensagem
        charMensagem[i] = (char) valorChar; // converte o binario do bit num array de char
      }

    } // fim do loop

    String mensagemDecodificada = new String(charMensagem); // cria uma string com o array de char decodificado

    return mensagemDecodificada;
  }// fim do metodo

  /**
   * exibe os bits de cada um dos inteiros dentro de um array
   * 
   * @param public static String exibirBitsStr(int[] mensagemBinaria) {
   * @return retorna uma string para vizualizacao da representacao de bits
   */
  public static String exibirBitsStr(int[] mensagemBit) {

    int marcara = 1 << 31; // uma mascara com o 1 mais a esquerda possivel
    String bitsString = ""; // string que vai exibir os bits

    for (int bits : mensagemBit) { // percorre toda a mensagem
      for (int i = 1; i <= 32; i++) { // percorre todos os 32 bits dos inteiros

        bitsString += (bits & marcara) == 0 ? "0" : "1"; // adiciona a string o bit equivalente

        bits = bits << 1; // desloca para a direita o inteiro, garantindo que percorra os 32 bits

        if (i % 8 == 0) { // a cada 8 bits um espaco vazio
          bitsString += " ";
        }
      }
    }

    return bitsString; // a string com a representacao dos bits

  }// fimd do metodo

  /**
   * metodo que transforma os bits compactados em um array de fluxo simples de
   * inteiro, SOMENTE UTILIZADO PARA REALIZAR A ANIMACAO
   * 
   * @param pacotesDeBits o array com os bits
   * @param totalDeBits   o total de bits armazenados
   * @return uma versao simplificada onde cada inteiro "eh" um bit.
   */
  public static int[] desempacotarBits(int[] pacotesDeBits, int totalDeBits) {
    // cria o array final que tera o tamanho exato do numero de bits
    if (ControlerTelaPrincipal.controlerTelaPrincipal.opcaoSelecionada() != 0) { // caso nao seja binario o numero total
                                                                                 // de bits eh o dobro
      totalDeBits = totalDeBits * 2;
    }

    int[] fluxoSimples = new int[totalDeBits];

    // percorre todos os bits do pacote e os adiciona a versao simplificada que sera
    // usada na animacao
    for (int i = 0; i < totalDeBits; i++) {
      // calcula em qual 'pacote' (inteiro do array) o bit esta
      int indiceDoPacote = i / 32;
      // calcula em qual posicao DENTRO do pacote o bit esta
      int posicaoNoPacote = 31 - (i % 32);
      // "pesca" o bit:
      int bit = (pacotesDeBits[indiceDoPacote] >> posicaoNoPacote) & 1;

      // armazena o bit extraido (0 ou 1) no nosso array de fluxo simples
      fluxoSimples[i] = bit;
    }

    return fluxoSimples; // uma versao simplificada utilizada para realizar a animacao da transmissaao
  }// fim do metodo

}// fim da classe
