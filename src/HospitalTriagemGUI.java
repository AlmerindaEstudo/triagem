import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

class Paciente {
    String nome;
    String nivel;
    int ordemChegada;
    int tempoEspera;

    public Paciente(String nome, String nivel, int ordemChegada) {
        this.nome = nome;
        this.nivel = nivel;
        this.ordemChegada = ordemChegada;
        this.tempoEspera = 0;
    }

    public void atualizarNivel(int tempoTotal) {
        if (nivel.equals("Amarelo") && tempoTotal >= 60) {
            nivel = "Vermelho";
        }
    }

    public String toString() {
        return nome + " - " + nivel + " - Espera estimada: " + tempoEspera + " min";
    }
}

public class HospitalTriagemGUI {
    private JFrame frame;
    private DefaultListModel<Paciente> modeloLista;
    private JList<Paciente> listaPacientes;
    private javax.swing.Timer timer; // <== aqui usa javax.swing.Timer

    private int tempoTotal = 0;
    private int ordemGlobal = 0;

    private Map<Integer, Paciente> arvorePacientes = new TreeMap<>();

    public HospitalTriagemGUI() {
        frame = new JFrame("Sistema de Triagem Hospitalar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        modeloLista = new DefaultListModel<>();
        listaPacientes = new JList<>(modeloLista);

        JTextField nomeField = new JTextField(10);
        String[] niveis = {"Verde", "Amarelo", "Vermelho"};
        JComboBox<String> nivelBox = new JComboBox<>(niveis);
        JButton adicionarBtn = new JButton("Adicionar Paciente");

        adicionarBtn.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Digite o nome do paciente.");
                return;
            }
            String nivel = (String) nivelBox.getSelectedItem();
            Paciente novo = new Paciente(nome, nivel, ordemGlobal++);
            arvorePacientes.put(novo.ordemChegada, novo);

            atualizarFila();
            nomeField.setText("");
        });

        JPanel painelSuperior = new JPanel();
        painelSuperior.add(new JLabel("Nome:"));
        painelSuperior.add(nomeField);
        painelSuperior.add(nivelBox);
        painelSuperior.add(adicionarBtn);

        frame.add(painelSuperior, BorderLayout.NORTH);
        frame.add(new JScrollPane(listaPacientes), BorderLayout.CENTER);

        // Timer Swing com fully qualified name
        timer = new javax.swing.Timer(20000, e -> {
            tempoTotal += 20;

            for (Paciente p : arvorePacientes.values()) {
                p.atualizarNivel(tempoTotal);
            }

            chamarProximo();
            atualizarFila();
        });
        timer.start();

        frame.setVisible(true);
    }

    // Aqui explicitamente usa java.util.List para evitar conflito com java.awt.List
    private java.util.List<Paciente> getFilaOrdenada() {
        java.util.List<Paciente> lista = new ArrayList<>(arvorePacientes.values());
        lista.sort((a, b) -> {
            if (a.nivel.equals(b.nivel)) return a.ordemChegada - b.ordemChegada;
            if (a.nivel.equals("Vermelho")) return -1;
            if (b.nivel.equals("Vermelho")) return 1;
            if (a.nivel.equals("Amarelo")) return -1;
            return 1;
        });
        return lista;
    }

    private void chamarProximo() {
        if (arvorePacientes.isEmpty()) return;

        java.util.List<Paciente> filaOrdenada = getFilaOrdenada();

        Paciente proximo = filaOrdenada.get(0);
        JOptionPane.showMessageDialog(frame, "Chamando paciente: " + proximo.nome + " (Prioridade: " + proximo.nivel + ")");
        arvorePacientes.remove(proximo.ordemChegada);
    }

    private void atualizarFila() {
        modeloLista.clear();

        java.util.List<Paciente> filaOrdenada = getFilaOrdenada();
        int tempoPorAtendimento = 20;
        int tempoAcumulado = 0;

        for (Paciente p : filaOrdenada) {
            p.tempoEspera = tempoAcumulado;
            modeloLista.addElement(p);
            tempoAcumulado += tempoPorAtendimento;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalTriagemGUI::new);
    }
}
