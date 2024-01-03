import java.awt.Color;
import java.util.Random;

import tester.Tester;
import javalib.funworld.*;
//import javalib.worldcanvas.WorldCanvas;
import javalib.worldimages.*;

// to represent the part where the random sequence is displayed by the computer
class QuestionMode extends World {
  ILoButton correct;
  ILoButton correctSequence;
  boolean drawLit; // true if we want to draw any one button lit

  QuestionMode(ILoButton correct, ILoButton correctSequence, boolean drawLit) {
    this.correct = correct;
    this.correctSequence = correctSequence;
    this.drawLit = drawLit;
  }

  // to draw all buttons dark if the correct sequence has been displayed
  // or to draw the buttons light or dark depending on if we want to light
  // the buttons on the next tick
  public WorldScene makeScene() {
    return this.correctSequence.drawQuestion(drawLit);
  }

  // to decide whether to light up or darken the buttons on the next tick
  // and to move to the answer class if we are done showing the correct sequence
  // answer class is where we accept and validate user response
  public World onTick() {
    return this.correctSequence.nextStep(drawLit, correct);
  }

}

// to represent the part where the user is allowed to respond and
// we validate if the response is correct
class AnswerMode extends World {
  ILoButton correctSequence;
  ILoButton userSequence;
  ButtonUnion bu;
  boolean drawLit;
  boolean startComparing; // true if we want to make comparisons on next tick
  Button last;

  AnswerMode(ILoButton correctSequence, ILoButton userSequence,
      ButtonUnion bu, boolean drawLit, boolean startComparing, Button last) {
    this.correctSequence = correctSequence;
    this.userSequence = userSequence;
    this.bu = bu;
    this.drawLit = drawLit;
    this.startComparing = startComparing;
    this.last = last;
  }

  // to draw the buttons depending on whether we want them all dark
  // or we want to highlight one of them
  public WorldScene makeScene() {
    WorldScene background = new WorldScene(100, 400);
    if (drawLit) {
      return background.placeImageXY(this.bu.drawLight(this.last), 50, 200);
    } else {
      return background.placeImageXY(this.bu.drawDark(), 50, 200);
    }
  }

  // to decide whether to allow the user to keep giving responses or not or move to
  // the next level depending on whether the user has given the correct answer
  // or is still answering correctly or has entered a wrong response
  public World onTick() {
    ILoButton nextSequence = this.correctSequence.generateNext();
    if (this.startComparing && this.correctSequence.exactSame(this.userSequence)) {
      return new QuestionMode(nextSequence, nextSequence, false);
    } else if (this.startComparing && this.correctSequence.partiallySame(this.userSequence)) {
      return new AnswerMode(this.correctSequence, this.userSequence, this.bu,
          false, false, this.last);
    } else if (this.startComparing) {
      return this.endOfWorld("You Lose!");
    } else {
      return this;
    }
  }

  // to find which button has been clicked and 
  // add it to the user sequence
  public World onMouseReleased(Posn pos) {
    Button red = new Button(Color.RED, 100, 100);
    Button blue = new Button(Color.BLUE, 100, 100);
    Button green = new Button(Color.GREEN, 100, 100);
    Button yellow = new Button(Color.YELLOW, 100, 100);

    if (new Utils().checkRange(pos, 0, 100)) {
      return new AnswerMode(this.correctSequence, this.userSequence.addToEnd(red),
          this.bu, true, true, red);
    } else if (new Utils().checkRange(pos, 100, 200)) {
      return new AnswerMode(this.correctSequence, this.userSequence.addToEnd(blue),
          this.bu, true, true, blue);
    } else if (new Utils().checkRange(pos, 200, 300)) {
      return new AnswerMode(this.correctSequence, this.userSequence.addToEnd(green),
          this.bu, true, true, green);
    } else {
      return new AnswerMode(this.correctSequence, this.userSequence.addToEnd(yellow),
          this.bu, true, true, yellow);
    }
  }

  // to display the ending scene if a user loses the game
  public WorldScene lastScene(String msg) {
    WorldScene background = new WorldScene(100, 400);
    return background.placeImageXY(new TextImage(msg, Color.BLUE), 50, 200);
  }
}

// Represents a list of buttons
interface ILoButton {
  Random RAND = new Random(5);
  Button RED = new Button(Color.RED, 100, 100);
  Button BLUE = new Button(Color.BLUE, 100, 100);
  Button GREEN = new Button(Color.GREEN, 100, 100);
  Button YELLOW = new Button(Color.YELLOW, 100, 100);

  // to add a new randomly generated button to the list
  public ILoButton generateNext();

  // to find if two lists are exactly the same
  boolean exactSame(ILoButton that);

  // to find whether this list of buttons is empty
  boolean sameMtLoButton();

  // to find whether this non empty list of buttons is same as that 
  // non empty list of buttons
  boolean sameConsLoButton(ConsLoButton that);

  // to find if that list has the first n elements of this list
  // where n is the length of that list
  boolean partiallySame(ILoButton that);

  //to find if that list has the first n elements of this list
  // where n is the length of that list
  boolean partiallySameHelper(Button first, ILoButton rest);

  // to add the given button to the end of this list of buttons
  ILoButton addToEnd(Button latest);

  // to help find the next state in the Question Mode class
  World nextStep(boolean drawLit, ILoButton correct);

  // to help draw the Question Mode depending on the world we have
  WorldScene drawQuestion(boolean drawLit);
} 

// Represents an empty list of buttons
class MtLoButton implements ILoButton {

  //to add a new randomly generated button to this empty list
  public ILoButton generateNext() {
    int num = RAND.nextInt(4);
    if (num == 0) {
      return new ConsLoButton(RED, this);
    } else if (num == 1) {
      return new ConsLoButton(BLUE, this);
    } else if (num == 2) {
      return new ConsLoButton(GREEN, this);
    } else {
      return new ConsLoButton(YELLOW, this);
    }
  }

  // to find if this empty list is same as that list
  public boolean exactSame(ILoButton that) {
    return that.sameMtLoButton();
  }

  //to find whether this list of buttons is empty
  public boolean sameMtLoButton() {
    return true;
  }

  //to find whether this empty list of buttons is same as that 
  // non empty list of buttons
  public boolean sameConsLoButton(ConsLoButton that) {
    return false;
  }

  //to find if that list has the first n elements of this list
  // where n is the length of that list
  public boolean partiallySame(ILoButton that) {
    return that.sameMtLoButton();
  }

  //to find if that list has the first n elements of this list
  // where n is the length of that list
  public boolean partiallySameHelper(Button first, ILoButton rest) {
    return true;
  }

  //to add the given button to the end of this list empty list of buttons
  public ILoButton addToEnd(Button latest) {
    return new ConsLoButton(latest, this);
  }

  // to go to the answer mode if the correct sequence is empty,
  // that is, we have displayed the whole sequence
  public World nextStep(boolean drawLit, ILoButton correct) {
    return new AnswerMode(correct, new MtLoButton(),
        new ButtonUnion(RED, BLUE, GREEN, YELLOW),
        false, false, new Button(Color.RED, 100, 100));
  }

  // to draw all buttons dark when the whole sequence has been displayed
  public WorldScene drawQuestion(boolean drawLit) {
    WorldScene background = new WorldScene(100, 400);

    return background.placeImageXY(new AboveImage(
        RED.drawDark(), BLUE.drawDark(), GREEN.drawDark(),
        YELLOW.drawDark()), 50, 200);
  }
}

// Represents a non-empty list of buttons
class ConsLoButton implements ILoButton {
  Button first;
  ILoButton rest;

  ConsLoButton(Button first, ILoButton rest) {
    this.first = first;
    this.rest = rest;
  }

  //to add a new randomly generated button to this list
  public ILoButton generateNext() {
    int num = RAND.nextInt(4);
    if (num == 0) {
      return new ConsLoButton(new Button(Color.RED, 100, 100), this);
    } else if (num == 1) {
      return new ConsLoButton(new Button(Color.BLUE, 100, 100), this);
    } else if (num == 2) {
      return new ConsLoButton(new Button(Color.GREEN, 100, 100), this);
    } else {
      return new ConsLoButton(new Button(Color.YELLOW, 100, 100), this);
    }
  }

  //to find if this list is same as that list
  public boolean exactSame(ILoButton that) {
    //return that.exactSameHelper(this.first, this.rest);
    return that.sameConsLoButton(this);
  }

  //to find whether this list of buttons is empty
  public boolean sameMtLoButton() {
    return false;
  }

  //to find whether this non empty list of buttons is same as that 
  // non empty list of buttons
  public boolean sameConsLoButton(ConsLoButton that) {
    return that.first.compareButton(this.first)
        && that.rest.exactSame(this.rest);
  }

  //to find if that list has the first n elements of this list
  // where n is the length of that list
  public boolean partiallySame(ILoButton that) {
    return that.partiallySameHelper(this.first, this.rest);
  }

  //to find if that list has the first n elements of this list
  // where n is the length of that list
  public boolean partiallySameHelper(Button first, ILoButton rest) {
    if (first.compareButton(this.first)) {
      return rest.partiallySame(this.rest);
    } else {
      return false;
    }
  }

  //to add the given button to the end of this list empty list of buttons
  public ILoButton addToEnd(Button latest) {
    return new ConsLoButton(this.first, this.rest.addToEnd(latest));
  }

  // to move to the next button in the sequence if we just displayed
  // the last correct button or to light up the next button if we 
  // just displayed all buttons dark
  public World nextStep(boolean drawLit, ILoButton correct) {
    if (drawLit) {
      return new QuestionMode(correct, this.rest, false);
    } else {
      return new QuestionMode(correct, this, true);
    }
  }

  //to draw the next button lit up or to show all buttons dark 
  public WorldScene drawQuestion(boolean drawLit) {
    WorldScene background = new WorldScene(100, 400);
    if (drawLit) {
      if (this.first.getColor() == Color.RED) {
        return background.placeImageXY(new AboveImage(
            RED.drawLit(), BLUE.drawDark(), GREEN.drawDark(),
            YELLOW.drawDark()), 50, 200);
      } else if (this.first.getColor() == Color.BLUE) {
        return background.placeImageXY(new AboveImage(
            RED.drawDark(), BLUE.drawLit(), GREEN.drawDark(),
            YELLOW.drawDark()), 50, 200);
      } else if (this.first.getColor() == Color.GREEN) {
        return background.placeImageXY(new AboveImage(
            RED.drawDark(), BLUE.drawDark(), GREEN.drawLit(),
            YELLOW.drawDark()), 50, 200);
      } else {
        return background.placeImageXY(new AboveImage(
            RED.drawDark(), BLUE.drawDark(), GREEN.drawDark(),
            YELLOW.drawLit()), 50, 200);
      }
    } else {
      return background.placeImageXY(new AboveImage(
          RED.drawDark(), BLUE.drawDark(), GREEN.drawDark(),
          YELLOW.drawDark()), 50, 200);
    }
  }
} 

// Represents one of the four buttons you can click
class Button {
  Color color;
  int x;
  int y;

  Button(Color color, int x, int y) {
    this.color = color;
    this.x = x;
    this.y = y;
  }

  // Draw this button dark
  WorldImage drawDark() {
    return this.drawButton(this.color.darker().darker());
  }

  // Draw this button lit
  WorldImage drawLit() {
    return this.drawButton(this.color.brighter().brighter());
  }

  // Draw a button
  WorldImage drawButton(Color c) {
    return new RectangleImage(this.x, this.y, OutlineMode.SOLID, c);
  }

  // to find if this button is same as that button
  boolean compareButton(Button that) {
    return this.color == that.color
        && this.x == that.x
        && this.y == that.y;
  }

  // to find the color of a button
  Color getColor() {
    return this.color;
  }

  // to draw a button
  WorldImage draw() {
    return new RectangleImage(this.x, this.y, OutlineMode.SOLID, this.color);
  }
}

class Utils {
  // to check the y coordinate of the click to find out which
  // button has been clicked
  public boolean checkRange(Posn pos, int min, int max) {
    return pos.y > min && pos.y <= max;
  }
}

// to collect all the buttons we want to draw in a single data structure
class ButtonUnion {
  Button b1;
  Button b2;
  Button b3;
  Button b4;

  ButtonUnion(Button b1, Button b2, Button b3, Button b4) {
    this.b1 = b1;
    this.b2 = b2;
    this.b3 = b3;
    this.b4 = b4;
  }

  // to draw a ButtonUnion where the given button is lit up
  public WorldImage drawLight(Button first) {
    Button red = new Button(Color.RED, 100, 100);
    Button blue = new Button(Color.BLUE, 100, 100);
    Button green = new Button(Color.GREEN, 100, 100);
    Button yellow = new Button(Color.YELLOW, 100, 100);

    if (first.getColor() == Color.RED) {
      return new AboveImage(red.drawLit(), blue.drawDark(), green.drawDark(), yellow.drawDark());
    } else if (first.getColor() == Color.BLUE) {
      return new AboveImage(red.drawDark(), blue.drawLit(), green.drawDark(), yellow.drawDark());
    } else if (first.getColor() == Color.GREEN) {
      return new AboveImage(red.drawDark(), blue.drawDark(), green.drawLit(), yellow.drawDark());
    } else {
      return new AboveImage(red.drawDark(), blue.drawDark(), green.drawDark(), yellow.drawLit());
    }
  }

  // to draw all four buttons dark
  public WorldImage drawDark() {
    return new AboveImage(this.b1.drawDark(), this.b2.drawDark(), this.b3.drawDark(),
        this.b4.drawDark());
  }
}

// Examples
class ExamplesSimon {
  Button buttonRed = new Button(Color.RED, 100, 100);
  Button buttonBlue = new Button(Color.BLUE, 100, 100);
  Button buttonGreen = new Button(Color.GREEN, 100, 100);
  Button buttonYellow = new Button(Color.YELLOW, 100, 100);

  WorldImage sample = new AboveImage(buttonRed.drawDark(),
      buttonBlue.drawDark(),
      buttonGreen.drawDark(),
      buttonYellow.drawDark());

  ILoButton list1 = new ConsLoButton(buttonRed,
      new ConsLoButton(buttonBlue,
          new ConsLoButton(buttonGreen,
              new ConsLoButton(buttonYellow, new MtLoButton()))));

  ILoButton list1rest = new ConsLoButton(buttonBlue,
      new ConsLoButton(buttonGreen,
          new ConsLoButton(buttonYellow, new MtLoButton())));

  ConsLoButton cons1 = new ConsLoButton(buttonRed,
      new ConsLoButton(buttonBlue,
          new ConsLoButton(buttonGreen,
              new ConsLoButton(buttonYellow, new MtLoButton()))));

  ILoButton sublist1 = new ConsLoButton(buttonRed,
      new ConsLoButton(buttonBlue,
          new ConsLoButton(buttonGreen, new MtLoButton())));

  ILoButton list2 = new ConsLoButton(buttonYellow,
      new ConsLoButton(buttonGreen,
          new ConsLoButton(buttonRed,
              new ConsLoButton(buttonYellow, new MtLoButton()))));

  ILoButton list3 = new ConsLoButton(buttonYellow, new MtLoButton());

  ILoButton mtList = new MtLoButton();

  ILoButton list5 = new ConsLoButton(buttonRed,
      new ConsLoButton(buttonBlue,
          new ConsLoButton(buttonGreen, new MtLoButton())));

  ILoButton list5rev = new ConsLoButton(buttonGreen,
      new ConsLoButton(buttonBlue,
          new ConsLoButton(buttonRed, new MtLoButton())));

  ILoButton list6 = new ConsLoButton(buttonYellow,
      new ConsLoButton(buttonRed, new MtLoButton()));

  ButtonUnion union1 = new ButtonUnion(this.buttonRed,
      this.buttonBlue, this.buttonGreen, this.buttonYellow);
  
  // to test the method exactSame() in classes MtLoButton and ILoButton
  public boolean testExactSame(Tester t) {
    return t.checkExpect(this.mtList.exactSame(this.mtList),true)
        && t.checkExpect(this.mtList.exactSame(this.list3), false)
        && t.checkExpect(this.list3.exactSame(this.list3), true)
        && t.checkExpect(this.list1.exactSame(this.list1), true)
        && t.checkExpect(this.list1.exactSame(this.mtList), false);
  }

  //to test the method sameMtLoButton() in classes MtLoButton and ILoButton
  public boolean testSameMtLoButton(Tester t) {
    return t.checkExpect(this.mtList.sameMtLoButton(),true)
        && t.checkExpect(this.list3.sameMtLoButton(), false);
  }

  //to test the method sameConsLoButton() in classes MtLoButton and ILoButton
  public boolean testSameConsLoButton(Tester t) {
    return t.checkExpect(this.mtList.sameConsLoButton(this.cons1),false)
        && t.checkExpect(this.list1.sameConsLoButton(this.cons1), true)
        && t.checkExpect(this.list2.sameConsLoButton(this.cons1), false);
  }

  //to test the method partiallySame() in classes MtLoButton and ILoButton
  public boolean testPartiallySame(Tester t) {
    return t.checkExpect(this.list1.partiallySame(this.mtList), true)
        && t.checkExpect(this.list1.partiallySame(this.list3), false)
        && t.checkExpect(this.list1.partiallySame(this.sublist1), true)
        && t.checkExpect(this.mtList.partiallySame(this.sublist1), false);
  } 

  //to test the method partiallySameHelper(Button, ILoButton) in classes MtLoButton and ILoButton
  public boolean testPartiallySameHelper(Tester t) {
    return t.checkExpect(this.mtList.partiallySameHelper(this.buttonRed, this.list1rest), true)
        && t.checkExpect(this.list1.partiallySameHelper(this.buttonRed, this.list1rest), true)
        && t.checkExpect(this.list2.partiallySameHelper(this.buttonRed, this.list1rest), false);
  }

  //to test the method addToEnd(Button) in classes MtLoButton and ILoButton
  public boolean testAddToEnd(Tester t) {
    return t.checkExpect(this.list3.addToEnd(this.buttonRed), this.list6)
        && t.checkExpect(this.mtList.addToEnd(this.buttonYellow), this.list3)
        && t.checkExpect(this.list5.addToEnd(this.buttonYellow), this.list1);
  }

  // to test the method drawButton() in class Button
  public boolean testDrawButton(Tester t) {
    return t.checkExpect(this.buttonRed.draw(), 
        new RectangleImage(100, 100, OutlineMode.SOLID, Color.RED))
        && t.checkExpect(this.buttonYellow.draw(), 
            new RectangleImage(100, 100, OutlineMode.SOLID, Color.YELLOW));
  }

  //to test the method drawDark() in class Button
  public boolean testDrawDark(Tester t) {
    return t.checkExpect(this.buttonRed.drawDark(), 
        new RectangleImage(100, 100, OutlineMode.SOLID, Color.RED.darker().darker()))
        && t.checkExpect(this.buttonYellow.drawDark(), 
            new RectangleImage(100, 100, OutlineMode.SOLID, Color.YELLOW.darker().darker()));
  }

  //to test the method drawLit() in class Button
  public boolean testDrawLit(Tester t) {
    return t.checkExpect(this.buttonRed.drawLit(), 
        new RectangleImage(100, 100, OutlineMode.SOLID, Color.RED.brighter().brighter()))
        && t.checkExpect(this.buttonYellow.drawLit(), 
            new RectangleImage(100, 100, OutlineMode.SOLID, Color.YELLOW.brighter().brighter()));
  }

  //to test the method compareButton(Button) in class Button
  public boolean testCompareButton(Tester t) {
    return t.checkExpect(this.buttonRed.compareButton(this.buttonRed), true)
        && t.checkExpect(this.buttonBlue.compareButton(this.buttonBlue), true)
        && t.checkExpect(this.buttonBlue.compareButton(this.buttonRed), false)
        && t.checkExpect(this.buttonBlue.compareButton(new Button(Color.BLUE, 100, 10)), false);
  }

  //to test the method getColor() in class Button
  public boolean testGetColor(Tester t) {
    return t.checkExpect(this.buttonBlue.getColor(), Color.BLUE)
        && t.checkExpect(this.buttonRed.getColor(), Color.RED);
  }

  //to test the method checkRange() in class Utils
  public boolean testCheckRange(Tester t) {
    return t.checkExpect((new Utils()).checkRange(new Posn(100, 200), 100, 200), true)
        && t.checkExpect((new Utils()).checkRange(new Posn(100, 200), 200, 300), false)
        && t.checkExpect((new Utils()).checkRange(new Posn(100, 200), 150, 250), true)
        && t.checkExpect((new Utils()).checkRange(new Posn(0, 100), 150, 250), false);
  }



  boolean testGo(Tester t) {
    ILoButton startButton = new MtLoButton().generateNext();
    QuestionMode myWorld = new QuestionMode(startButton, startButton, false);
    return myWorld.bigBang(100,400,0.5);
  }
}