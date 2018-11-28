/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.skins.ModernSkin;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GLLSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;

/**
 *
 * @author Basma
 */
public class CarMeter extends Application implements MapComponentInitializedListener {
    GoogleMapView mapView;
    GoogleMap map;
    SerialCommunication serialComm ;
    MarkerOptions markerOptions;
    Marker marker ;
    double i = 0.0001;
    double latitude=30.0813565,longitude=31.2383316;

    @Override
    public void init(){
        try {
            serialComm=new SerialCommunication();
            serialComm.connect();
            Thread th = new Thread(new ReadLine());
            th.start();
         
        } catch (Exception ex) {
            System.out.println("Init Exception");
            ex.printStackTrace();
        }
    }
   @Override
    public void start(Stage primaryStage) throws Exception {
 
        Button button1 = new Button("start");
        Button button2 = new Button("stop");
        Button button3 = new Button("Button 3");
        
      button1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
       
            }
        });
      
       button2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                
            }
        });
       
        button3.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                
            }
        });

        Gauge gauge = GaugeBuilder.create()
//                         .skin(ModernSkin.class)
                         .sections(new Section(85, 90, "", Color.rgb(204, 0, 0, 0.5)),
                                   new Section(90, 95, "", Color.rgb(204, 0, 0, 0.75)),
                                   new Section(95, 100, "", Color.rgb(204, 0, 0)))
                         .title("Speed")
                         .unit("UNIT")
                         .threshold(85)
                         .thresholdVisible(true)
                         .animated(true)
                         .build();
        
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

        gridPane.add(button1, 0, 0, 1, 1);
        gridPane.add(button2, 1, 0, 1, 1);
        gridPane.add(button3, 2, 0, 1, 1);
        gridPane.add(gauge, 3, 3, 2, 2);
   
        Label lattitude = new Label("lattitude :");
         lattitude.setFont(Font.font("Tahoma", FontWeight.THIN, 20));
          gridPane.add(lattitude, 2, 10, 1, 1);
          
          TextField text = new TextField ();
          gridPane.add(text, 3, 10, 1, 1);
        
         Label longitude = new Label("longitude :");
         longitude.setFont(Font.font("Tahoma", FontWeight.THIN,20));
         gridPane.add(longitude, 2, 15, 1, 1);
          TextField text2 = new TextField ();
          gridPane.add(text2, 3, 15, 1, 1);
         
         Label speed = new Label("Speed :");
         speed.setFont(Font.font("Tahoma", FontWeight.THIN, 20));
         gridPane.add(speed, 2, 20, 1, 1);
          TextField text3 = new TextField ();
          gridPane.add(text3, 3, 20, 1, 1);
          
          gridPane.setGridLinesVisible(true);
 //__________________________________________________________________________________________
          
          HBox hbox = new HBox();
          mapView = new GoogleMapView();
        mapView.addMapInializedListener(this);
 
        BorderPane borderPane = new BorderPane();
        //borderPane.getChildren().addAll();
      
        borderPane.setLeft(mapView);
        BorderPane border = new BorderPane();
   
        
        hbox.getChildren().addAll(mapView,gridPane);
        
        Scene scene = new Scene(hbox,1300,600);
        
        

        primaryStage.setTitle("CarMeter");
        primaryStage.getIcons().add(new Image("maps.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void mapInitialized() {
    //Set the initial properties of the map.
    MapOptions mapOptions = new MapOptions();
    mapOptions.center(new LatLong(30.08056024, 31.23717248))
            .mapType(MapTypeIdEnum.ROADMAP)
            .overviewMapControl(false)
            .panControl(false)
            .rotateControl(false)
            .scaleControl(false)
            .streetViewControl(false)
            .zoomControl(false)
            .zoom(13);

    map = mapView.createMap(mapOptions);
    markerOptions = new MarkerOptions();
                    markerOptions.position( new LatLong(latitude, longitude) )
                                .visible(Boolean.TRUE)
                                .title("My Marker");

                    marker = new Marker( markerOptions );
                    map.addMarker(marker);
                    map.removeMarker(marker);
                    
    Thread t = new Thread( () -> {
        while (true){
           try {
               Thread.sleep(2000);
               //System.out.println("Calling showDirections from Java");
               Platform.runLater(() -> {
                        map.removeMarker(marker);
                       markerOptions = new MarkerOptions();
                       markerOptions.position( new LatLong(latitude,longitude) )
                        .visible(Boolean.TRUE)
                        .title("My Marker");
                         marker = new Marker( markerOptions );
                        map.addMarker(marker);
                        
                       });
           } catch( Exception ex ) {
               ex.printStackTrace();
           }
    }});
        t.start();

}
  
    class ReadLine implements Runnable 
    {
        public void run ()
        {  
            while(true)
            {
            try {
                while(serialComm.buf != null &&((serialComm.temp = serialComm.buf.readLine()) != null)){
                    if (SentenceValidator.isValid(serialComm.temp)) {
                    //System.out.println(serialComm.temp );
                    
                    SentenceFactory sf = SentenceFactory.getInstance();
                    Sentence s= sf.createParser(serialComm.temp);
                    //String id =s.getSentenceId();
                    //System.out.println(id );
    
                    if("GLL".equals(s.getSentenceId())) {
				GLLSentence gll = (GLLSentence) s;
				//System.out.println("GLL position: " + gll.getPosition());
                    } else if ("GGA".equals(s.getSentenceId())) {
                            GGASentence gga = (GGASentence) s;
                            latitude=gga.getAltitude();
                            longitude = gga.getGeoidalHeight();
                            System.out.println("GGA position: " + gga.getPosition());
                    }
                        
                          }
                }
            } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }      
        
        }
    }
 
    
    public static void main(String[] args) {
        
        launch(args);
    }

    
    
}