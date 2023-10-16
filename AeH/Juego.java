// RIGS 7E 20310388
import javax.swing.*;
import java.awt.event.*;
import java.awt.Font;
import java.awt.*;
import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Juego extends JPanel implements ActionListener {
	Font fuente2=new Font("Arial",Font.PLAIN,18);
    private static int questionIndex;
    private static int questionNumber;
    private static int P=0;
    private static boolean infoDisplayed = false;
    private static List<String> questions = new ArrayList<>();
    private static String[] answers;
    private static List<Integer> shuffledIndexes = new ArrayList<>();
    private static String userSelections = "";
	public JButton botonJ;

    public Juego() {
        setBackground(new Color(200, 242, 144));
		botonJ = new JButton("Empezar");
		botonJ.setBounds(300,250,150,30);
		botonJ.setFont(fuente2);
		add(botonJ);
		botonJ.addActionListener(this);
    }
    public void actionPerformed(ActionEvent accion){
    	if (accion.getSource() == botonJ){
			cargarPreguntasDesdeArchivo("preguntas.txt");
	        answers = new String[questions.size()];
	        questionIndex = 0;
	        questionNumber = 1;
	        generarIndicesAleatorios();
	        SistemaExperto_inicio();
		}
    }
    public static void SistemaExperto_inicio() {
        String[] options = {"Iniciar sistema experto", "Salir"};

        int response = JOptionPane.showOptionDialog(null, "Bienvenido a Adivina el héroe\n\nLa dinámica es la siguiente:\n" +
                "     * Te presentaré algunas preguntas sobre superhéroes.\n" +
                "     * Debes memorizar uno de ellos recordando siempre sus características, y yo trataré de adivinar\n" +
                "      mediante preguntas en cuál estás pensando.\n" +
                "     * Si tu respuesta es afirmativa, haz clic en 'Si', si es negativa, haz clic en 'No'.\n\n" +
                "Elige una opción para comenzar:", "Sistema Experto de Adivinanzas", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (response == 0) {
            Adivinar();
        } else {
            JOptionPane.showMessageDialog(null, "\n*******-Nos vemos luego-*******");
            System.exit(0);
        }
    }

    public static void Adivinar() {
        if (questionIndex < questions.size()) {
            int randomIndex = shuffledIndexes.get(questionIndex);
            
            int response = JOptionPane.showOptionDialog(null, "Pregunta #" + questionNumber + "\n" + questions.get(P), "Pregunta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Si", "No"}, null);
			
            if (response == 0) {
                answers[randomIndex] = "Si";
                // Agrega la selección del usuario a la variable global
                userSelections += "Si";
            } else if (response == 1) {
                answers[randomIndex] = "No";
                // Agrega la selección del usuario a la variable global
                userSelections += "No";
            } else {
                JOptionPane.showMessageDialog(null, "Debes seleccionar 'Si' o 'No'.");
                Adivinar();
                return;
            }

            guardarRespuesta("Pregunta #" + questionNumber + ": " + questions.get(P) + ": " + answers[randomIndex]);
            questionIndex++;
            questionNumber++;
            P++;
            Adivinar();
        } else {
            // Agrega tu código para buscar el resultado especial en el archivo
            String nombreArchivo = "solo.txt"; // Cambia esto al nombre de tu archivo
            String codigoBuscado = userSelections; // El código que deseas buscar

            boolean encontrado = false;

            try {
                BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo));
                String linea;
                while ((linea = reader.readLine()) != null) {
                    if (linea.contains(codigoBuscado)) {
                        int startIndex = linea.indexOf(codigoBuscado);
                        String nombre = linea.substring(0, startIndex);

                        // Muestra el nombre encontrado
                        JOptionPane.showMessageDialog(null, "Nombre encontrado: " + nombre);
                        encontrado = true;
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Si no se encontró una coincidencia, solicita al usuario que ingrese el nombre del Personaje
            if (!encontrado) {
                String nombreHeroe = JOptionPane.showInputDialog("El Personaje no se encontró en el archivo. Por favor, ingresa el nombre del Personaje:");

                // Guarda el nombre del Personaje en el archivo "result.txt"
                guardarNombreHeroe(nombreHeroe);

                // Guarda el registro en el archivo "solo.txt"
                guardarRespuestaSolo(nombreHeroe + ":" + userSelections);
            }

            JuguemosNuevamente();

            // Reinicia la variable global para el próximo juego
            userSelections = "";
        }
    }

    // Método para guardar el nombre del Personaje en el archivo "result.txt"
    public static void guardarNombreHeroe(String nombre) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt", true)); // Abre el archivo en modo append (agregar al final)
            writer.write(nombre); // Escribe el nombre del Personaje
            writer.newLine(); // Nueva línea
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String cargarUnResultadoDesdeArchivo(String archivo) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            String result = reader.readLine(); // Leer solo la primera línea
            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al cargar el resultado";
        }
    }

    public static void JuguemosNuevamente() {
        String[] options = {"Si", "No"};

        int response = JOptionPane.showOptionDialog(null, "¿Quieres jugar de nuevo?", "Reinicio", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (response == JOptionPane.YES_OPTION) {
            questionIndex = 0;
            questionNumber = 1; // Restablecer el número de pregunta para el próximo juego
            generarIndicesAleatorios(); // Generamos nuevos indices aleatorios para el próximo juego
            SistemaExperto_inicio();
        } else {
            JOptionPane.showMessageDialog(null, "\n*******-Nos vemos luego-*******");
            System.exit(0);
        }
    }

    public static void guardarRespuesta(String respuesta) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("basededato.txt", true));
            writer.write(respuesta);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void guardarRespuestaSolo(String respuesta) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("solo.txt", true));
            writer.write(respuesta);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void guardarResultadoEnArchivo(String result) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"));
            writer.write(result);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cargarPreguntasDesdeArchivo(String archivo) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            String line;
            while ((line = reader.readLine()) != null) {
                questions.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String cargarInformacionDesdeArchivo(String archivo) {
        StringBuilder informacion = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            String line;
            while ((line = reader.readLine()) != null) {
                informacion.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return informacion.toString();
    }

    public static void generarIndicesAleatorios() {
        shuffledIndexes.clear();
        for (int i = 0; i < questions.size(); i++) {
            shuffledIndexes.add(i);
        }
        Collections.shuffle(shuffledIndexes);
    }

    public static void main(String[] args) {
		Reglas base=new Reglas();
		base.setBounds(0,0,800,600);
		base.setVisible(true);
    }
}
