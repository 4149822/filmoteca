package dam.alumno.filmoteca;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Locale;

public class PeliculaController {

    @FXML
    private TextField campoFecha;

    @FXML
    private Label campoId;

    @FXML
    private TextField campoTitulo;

    @FXML
    private TextArea campoDescripcion;

    @FXML
    private Slider campoValor;

    @FXML
    private TextField campoPoster;

    @FXML
    private Label textoValor;

    private Pelicula pelicula = null;
    private DatosFilmoteca datosFilmoteca = DatosFilmoteca.getInstancia();
    private ObservableList<Pelicula> listaPeliculas = datosFilmoteca.getListaPeliculas();

    @FXML
    public void initialize() {
        campoFecha.setTextFormatter(new TextFormatter<>(c -> c.getText().matches("\\d*") ? c : null));
        textoValor.textProperty().bind(campoValor.valueProperty().asString(Locale.US,"%.1f"));
    }

    public void set(Pelicula pelicula) {
        this.pelicula = pelicula;

        campoId.setText(String.valueOf(pelicula.getId()));
        campoTitulo.setText(pelicula.getTitle());
        campoDescripcion.setText(pelicula.getDescription());
        campoFecha.setText(String.valueOf(pelicula.getYear()));
        campoValor.setValue(pelicula.getRating());
        campoPoster.setText(pelicula.getPoster());
    }

    public void onConfirmar(ActionEvent actionEvent) {
        if (campoFecha.getText().isBlank() || campoTitulo.getText().isBlank()) { // Estos campos deberian ser obligatorios
            new Alert(Alert.AlertType.ERROR, "Campo invalido").showAndWait();
            return;
        }

        if (pelicula == null) {
            Pelicula nuevaPelicula = new Pelicula();
            // Obtenemos un nuevo ID sumandole uno al ID mayor en la lista.
            nuevaPelicula.setId(listaPeliculas.stream().mapToInt(Pelicula::getId).max().orElse(0) + 1);
            nuevaPelicula.setTitle(campoTitulo.getText());
            nuevaPelicula.setDescription(campoDescripcion.getText());
            nuevaPelicula.setYear(Integer.parseInt(campoFecha.getText()));
            nuevaPelicula.setRating(Float.parseFloat(textoValor.getText()));
            nuevaPelicula.setPoster(campoPoster.getText());
            listaPeliculas.add(nuevaPelicula);
        } else {
            pelicula.setTitle(campoTitulo.getText());
            pelicula.setDescription(campoDescripcion.getText());
            pelicula.setYear(Integer.parseInt(campoFecha.getText()));
            pelicula.setRating(Float.parseFloat(textoValor.getText()));
            pelicula.setPoster(campoPoster.getText());
        }

        ((Stage)((Node)(actionEvent.getSource())).getScene().getWindow()).close();
    }

}
