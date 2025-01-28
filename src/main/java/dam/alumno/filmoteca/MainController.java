package dam.alumno.filmoteca;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    private DatosFilmoteca datosFilmoteca = DatosFilmoteca.getInstancia();
    private ObservableList<Pelicula> listaPeliculas = datosFilmoteca.getListaPeliculas();
    private FilteredList<Pelicula> listaFiltrada;

    @FXML
    private TableView<Pelicula> tablaPeliculas;

    @FXML
    private TableColumn<Pelicula, Integer> columnaId;

    @FXML
    private TableColumn<Pelicula, String> columnaTitulo;

    @FXML
    private TableColumn<Pelicula, Integer> columnaFecha;

    @FXML
    private TableColumn<Pelicula, Float> columnaValor;

    @FXML
    private ImageView imagenPoster;

    @FXML
    private Label textoValor;

    @FXML
    private Label textoTitulo;

    @FXML
    private TextArea textoDescripcion;

    @FXML
    private Label textoFecha;

    @FXML
    private TextArea textoUrl;

    @FXML
    private Slider sliderValor;

    @FXML
    private TextField campoFiltro;

    @FXML
    public void initialize() {
        columnaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaTitulo.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("year"));
        columnaValor.setCellValueFactory(new PropertyValueFactory<>("rating"));

        listaFiltrada = new FilteredList<>(listaPeliculas, p -> true);
        tablaPeliculas.setItems(listaFiltrada);

        tablaPeliculas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) actualizarInfo(newValue);
        });

        // Filtro
        campoFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(pelicula -> {
                if (newValue == null || newValue.isEmpty()) return true; // mostrar todas si el filtro esta vacio
                return pelicula.getTitle().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }

    private void actualizarInfo(Pelicula newValue) {
        try {
            imagenPoster.setImage(new Image(newValue.getPoster()));
        } catch (java.lang.IllegalArgumentException e) {
            // URL invalida
            imagenPoster.setImage(null);
        }
        textoUrl.setText(newValue.getPoster());
        textoValor.setText(String.valueOf(newValue.getRating()));
        sliderValor.setValue(newValue.getRating());
        textoTitulo.setText(newValue.getTitle());
        textoFecha.setText(String.valueOf(newValue.getYear()));
        textoDescripcion.setText(newValue.getDescription());
    }

    @FXML
    public void onCerrar(ActionEvent actionEvent) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Cerrar aplicación");
        alerta.setHeaderText("¿Estás seguro de que quieres cerrar la aplicación?");
        alerta.showAndWait().ifPresent(response -> Platform.exit());
    }

    @FXML
    public void onBorrar(ActionEvent actionEvent) {
        if (tablaPeliculas.getSelectionModel().getSelectedIndex() == -1) return;

        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Borrar película");
        alerta.setHeaderText("¿Estás seguro de que deseas borrar esta película?");
        alerta.showAndWait().ifPresent(response -> {
            listaPeliculas.remove(tablaPeliculas.getSelectionModel().getSelectedItem());
        });
    }

    private void mostrarVentana(boolean peliculaExistente) {
        Scene escena = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PeliculaView.fxml"));
        try {
            escena = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        PeliculaController peliculaController = loader.getController();
        int count = listaPeliculas.size(); // luego veras por que hago esto en el ultimo comentario de esta función
        if (peliculaExistente) peliculaController.set(tablaPeliculas.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        stage.setTitle(peliculaExistente ? "Modificar película" : "Añadir nueva película");
        stage.setScene(escena);
        stage.showAndWait();

        if (peliculaExistente) {
            actualizarInfo(tablaPeliculas.getSelectionModel().getSelectedItem()); // Si no, no se actualizan los datos de la derecha
        } else if (listaPeliculas.size() != count) { // compruebo que la he añadido y no solo he cerrado la ventana
            campoFiltro.setText("");
            tablaPeliculas.getSelectionModel().selectLast();
        }
    }

    @FXML
    public void onModificar(ActionEvent actionEvent) {
        if (tablaPeliculas.getSelectionModel().getSelectedIndex() == -1) return;
        mostrarVentana(true);
    }

    @FXML
    public void onNuevo(ActionEvent actionEvent) {
        mostrarVentana(false);
    }
}