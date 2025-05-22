import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

class Produto {
    String nome;
    double custo;
    double lucro;
    int estoque;
    double precoVenda;

    public Produto(String nome, double custo, double lucro, int estoque) {
        this.nome = nome;
        this.custo = custo;
        this.lucro = lucro;
        this.estoque = estoque;
        this.precoVenda = custo * (1 + lucro / 100);
    }

    @Override
    public String toString() {
        return nome + " | Custo: R$" + custo + " | Lucro: " + lucro + "% | Venda: R$" + precoVenda + " | Estoque: " + estoque;
    }
}

public class CadastroProduto extends JFrame {
    private JTextField nomeField, custoField, lucroField, estoqueField, precoVendaField;
    private DefaultListModel<String> produtoListModel;
    private JList<String> produtoList;
    private java.util.List<Produto> produtos;

    public CadastroProduto() {
        produtos = new ArrayList<>();

        setTitle("Cadastro de Produto");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Nome do Produto:"));
        nomeField = new JTextField();
        inputPanel.add(nomeField);

        inputPanel.add(new JLabel("Preço de Custo:"));
        custoField = new JTextField();
        inputPanel.add(custoField);

        inputPanel.add(new JLabel("Lucro Desejado (%):"));
        lucroField = new JTextField();
        inputPanel.add(lucroField);

        inputPanel.add(new JLabel("Estoque:"));
        estoqueField = new JTextField();
        inputPanel.add(estoqueField);

        inputPanel.add(new JLabel("Preço de Venda (calculado):"));
        precoVendaField = new JTextField();
        precoVendaField.setEditable(false);
        inputPanel.add(precoVendaField);

        JButton calcularBtn = new JButton("Calcular Preço de Venda");
        calcularBtn.addActionListener(e -> calcularPrecoVenda());
        inputPanel.add(calcularBtn);

        JButton salvarBtn = new JButton("Salvar Produto");
        salvarBtn.addActionListener(e -> salvarProduto());
        inputPanel.add(salvarBtn);

        add(inputPanel, BorderLayout.NORTH);

        produtoListModel = new DefaultListModel<>();
        produtoList = new JList<>(produtoListModel);
        JScrollPane scrollPane = new JScrollPane(produtoList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Produtos Cadastrados"));

        add(scrollPane, BorderLayout.CENTER);

        carregarDoArquivo();
        setVisible(true);
    }

    private void calcularPrecoVenda() {
        try {
            double custo = Double.parseDouble(custoField.getText());
            double lucro = Double.parseDouble(lucroField.getText());
            double precoVenda = custo * (1 + lucro / 100);
            precoVendaField.setText(String.format("%.2f", precoVenda));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores inválidos para custo ou lucro.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarProduto() {
        try {
            String nome = nomeField.getText();
            double custo = Double.parseDouble(custoField.getText());
            double lucro = Double.parseDouble(lucroField.getText());
            int estoque = Integer.parseInt(estoqueField.getText());

            Produto produto = new Produto(nome, custo, lucro, estoque);
            produtos.add(produto);
            produtoListModel.addElement(produto.toString());
            salvarEmArquivo();

            JOptionPane.showMessageDialog(this, "Produto salvo com sucesso!");
            limparCampos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos corretamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarEmArquivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("produtos.txt"))) {
            for (Produto p : produtos) {
                writer.write(p.nome + ";" + p.custo + ";" + p.lucro + ";" + p.estoque);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar no arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarDoArquivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader("produtos.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 4) {
                    String nome = partes[0];
                    double custo = Double.parseDouble(partes[1]);
                    double lucro = Double.parseDouble(partes[2]);
                    int estoque = Integer.parseInt(partes[3]);
                    Produto p = new Produto(nome, custo, lucro, estoque);
                    produtos.add(p);
                    produtoListModel.addElement(p.toString());
                }
            }
        } catch (IOException e) {
            System.out.println("Nenhum produto salvo anteriormente.");
        }
    }

    private void limparCampos() {
        nomeField.setText("");
        custoField.setText("");
        lucroField.setText("");
        estoqueField.setText("");
        precoVendaField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CadastroProduto::new);
    }
}
