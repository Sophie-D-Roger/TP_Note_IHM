package sample;

import javax.swing.event.DocumentEvent.EventType; 

import javafx.beans.value.ChangeListener;    
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Controller {

	@FXML
	private RadioButton btnSelect;

	@FXML
	private RadioButton btnEllipse;

	@FXML
	private RadioButton btnRectangle;

	@FXML
	private RadioButton btnLine;

	@FXML
	private ColorPicker colorPicker;

	@FXML
	private Button btnDelete;

	@FXML
	private Button btnClone;

	@FXML
	private AnchorPane canvas;

	







	
	double tolerance=5;
	//cette méthode permet de déterminer si l'endroit où clique la souris se trouve sur la ligne qu'on veut sélectionner
	private boolean isOnLine(double mx, double my, Line l) {

		if(l.getEndX() != l.getStartX() && l.getEndX()-l.getStartX() > l.getEndY()-l.getStartY()) {
			double pente= (l.getEndY()-l.getStartY())/(l.getEndX()-l.getStartX());
			double decalage= l.getStartY()-pente*l.getStartX();

			return(pente*mx+decalage-tolerance <= my) && (pente*mx+decalage+tolerance >= my)
					&& (mx<=Math.max(l.getEndX(), l.getStartX())) && (mx>=Math.min(l.getEndX(), l.getStartX()));
		}
		else if(l.getEndY() != l.getStartY()) {
			double pente= (l.getEndX()-l.getStartX())/(l.getEndY()-l.getStartY());
			double decalage= l.getStartX()-pente*l.getStartY();

			return(pente*my+decalage-tolerance <= mx) && (pente*my+decalage+tolerance >= mx)
					&& (my<=Math.max(l.getEndY(), l.getStartY())) && (my>=Math.min(l.getEndY(), l.getStartY()));
		}
		else {
			return((mx<=l.getStartX()+tolerance) && (mx>=l.getStartX()-tolerance) && 
					(my<=l.getStartY()+tolerance) && (my>=l.getStartY()-tolerance));
		}
	}

	
	//cette méthode permet de déterminer si l'endroit où clique la souris se trouve dans le rectangle qu'on veut sélectionner
	public boolean isOnRectangle(double mx, double my, Rectangle r) {
		return r.contains(mx, my);
	}





	//permet de montrer qu'un élément est sélectionné, en lui affectant un effet Glow
	public void selection(Node nouv) {
		if(currentSelect!=null) {
			Glow pasGras = new Glow();
			pasGras.setLevel(0);
			currentSelect.setEffect(pasGras);
		}
		Glow gras = new Glow();
		gras.setLevel(1.5);
		nouv.setEffect(gras);
		currentSelect = nouv;
	}

	
	
	//applique un offset à la ligne passée en paramètre
	public void applyOffsetLine(Line l) {
		l.setStartX(l.getStartX()+l.getTranslateX());
		l.setStartY(l.getStartY()+l.getTranslateY());
		l.setEndX(l.getEndX()+l.getTranslateX());
		l.setEndY(l.getEndY()+l.getTranslateY());
		l.setTranslateX(0);
		l.setTranslateY(0);

	}
	
	//applique un offset au rectangle passé en paramètre
	public void applyOffsetRect(Rectangle r) {
		r.setX(r.getX()+r.getTranslateX());
		r.setY(r.getY()+r.getTranslateY());
		r.setTranslateX(0);
		r.setTranslateY(0);
	}


	
	//retourne une copie de l'objet passé en paramètre, avec un décalage choisi
	public Node cloneShape(Node n,int decalage) {
		if(n instanceof Line) {
			Line lt = (Line)n;
			Line ln = new Line(lt.getStartX()+decalage,lt.getStartY()+decalage,lt.getEndX()+decalage,lt.getEndY()+decalage);
			ln.setStroke(lt.getStroke());
			return ln;
		}
		else if(n instanceof Rectangle) {
			Rectangle rt =(Rectangle)n;
			Rectangle rn = new Rectangle(rt.getX()+decalage,rt.getY()+decalage,rt.getWidth(),rt.getHeight());
			rn.setFill(rt.getFill());
			return rn;
		}
		return null;
	}








	//initialisation de divers variables servant aux différentes fonctionnalités

	boolean clicOnSelect=false;
	double offsetX=0;
	double offsetY=0;
	Node currentSelect = null;

	Line l = new Line();
	Rectangle r = new Rectangle();

	
	//dessin des formes grâce à la souris
	@FXML
	public void drawShapes() {
		
		//gestion de l'event où la souris est appuyée dessus
		canvas.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() { 
			@Override 
			public void handle(MouseEvent e) { 
				clicOnSelect=false;
				if(btnSelect.isSelected()) {		
					offsetX=e.getX();
					offsetY=e.getY();

					for(int i=0; i< canvas.getChildren().size(); i++) {

						if(canvas.getChildren().get(i) instanceof Line) {    //si l'objet est une ligne
							Line line = (Line)canvas.getChildren().get(i);
							if(isOnLine(e.getX(), e.getY(), line)) {		//si le curseur est sur l'objet au moment du clic
								clicOnSelect=true;							//l'objet est sélectionné
								selection(line);

								break;
							}
						}
						else if(canvas.getChildren().get(i) instanceof Rectangle) {
							Rectangle rect = (Rectangle)canvas.getChildren().get(i);
							if(isOnRectangle(e.getX(),e.getY(), rect)) {
								clicOnSelect=true;
								selection(rect);
							}
						}

					}
				}else {		//initialisation des valeurs temporaires des objets qui seront utiisés dans le drag

					
					if(btnLine.isSelected()) {
						l.setStroke(colorPicker.getValue());
						l.setStartX(e.getX());
						l.setStartY(e.getY());
						l.setEndX(e.getX());
						l.setEndY(e.getY());
						canvas.getChildren().add(l);
					}
					else if(btnRectangle.isSelected()) {
						r.setFill(colorPicker.getValue());
						r.setX(e.getX());
						r.setY(e.getY());
						offsetX=e.getX();
						offsetY=e.getY();
						r.setHeight(0);
						r.setWidth(0);
						canvas.getChildren().add(r);
					}
				}
			} 
		}); 
		
		
		//gestion de l'event où la souris drag
		canvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() { 
			@Override 
			public void handle(MouseEvent e) { 
				if(btnSelect.isSelected()) {
					if(clicOnSelect) {
						currentSelect.setTranslateX(e.getX()-offsetX);
						currentSelect.setTranslateY(e.getY()-offsetY);

					}
				}
				else {		//update les différents objets en fonction des déplacements de la souris
					if(btnLine.isSelected()) {
						l.setEndX(e.getX());
						l.setEndY(e.getY());
					}
					else if(btnRectangle.isSelected()) {
						if(e.getY()-offsetY>0) {
							r.setHeight(e.getY()-offsetY);
						}
						else {
							r.setHeight(offsetY-e.getY());
							r.setY(e.getY());
						}

						if(e.getX()-offsetX>0) {
							r.setWidth(e.getX()-offsetX);
						}
						else {
							r.setWidth(offsetX-e.getX());
							r.setX(e.getX());
						}
					}
				}
			}
		});
		
		
		//gestion de l'event où on arrête d'appuyer sur la souris
		canvas.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() { 
			@Override 
			public void handle(MouseEvent e) { 
				if(btnSelect.isSelected()) {
					if(currentSelect instanceof Line) {
						applyOffsetLine((Line)currentSelect);
					}
					else if(currentSelect instanceof Rectangle) {
						applyOffsetRect((Rectangle)currentSelect);
					}
				}

				else { 		//création des versions finales des objets une fois que la souris est relâchée
					if(btnLine.isSelected()) {
						canvas.getChildren().remove(l);
						canvas.getChildren().add(cloneShape(l,0));
					}
					else if(btnRectangle.isSelected()) {
						canvas.getChildren().remove(r);
						canvas.getChildren().add(cloneShape(r,0));
					}
				}
			}
		});





	}











	@FXML
	public void initialize() {



		//gestion des changements de couleur des objets avec les valeurs du color picker
		colorPicker.setOnAction(new EventHandler() {
			@Override 
			public void handle(Event t) {
				Color c = colorPicker.getValue();
				if(currentSelect instanceof Line) {
					Line lt= (Line)currentSelect;
					lt.setStroke(c);
				}
				else if(currentSelect instanceof Rectangle) {
					Rectangle rt = (Rectangle)currentSelect;
					rt.setFill(c);
				}
			}
		});


		//gestion du bouton delete, en appuyant dessus, l'objet sélectionné est effacé du pane
		btnDelete.addEventHandler(MouseEvent.MOUSE_PRESSED, 
				new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e) {
				if(currentSelect !=null) {
					canvas.getChildren().remove(currentSelect);
					currentSelect= null;
				}

			}
		});


		
		//gestion du bouton clone, en appuyant dessus, une copie de l'objet sélectionné est créée et affichée dans le pane
		btnClone.addEventHandler(MouseEvent.MOUSE_PRESSED, 
				new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e) {
				if(currentSelect !=null) {
					canvas.getChildren().add(cloneShape(currentSelect,5));

				}

			}
		});



		drawShapes();


	}
}
