CardsUI
===================
An open source library offering the popular Google Now & Google Play cards views, additionnal features, view recycling and an optional Model-View-Controller structure.

## Google Play Cards
This new type of card based on the new Google play design are highly customizable.

The parameters include :

* Title text (String)
* Description text (String)
* Title color (String)
* Stripe color (String)
* Menu overflow (Boolean)
* Touch feedback on click (Boolean)

Here is an example : 

    mCardView.addCard(new MyPlayCard(
            "Different Colors for Title & Stripe", 
            "You can set any color for the title and any other color for the left stripe", 
            "#f2a400", "#9d36d0", true, false));
            
            
## Additionnal Features
In addition to the Google Play cards, I made a few changes to the original library by [Nadav Fima](https://github.com/nadavfima/cardsui-for-android).

Those changes include :


* **Setting regular cards's description text programmatically**

Called like this :

    mCardView.addCard(new MyCard("title string", "description string");
    
    
* **Setting stack titles's color programmatically_**

Called like this :

    CardStack stack = new CardStack();
    stack.setTitle("Card Title");
    stack.setColor("#33b5e5");
    mCardView.addStack(stack);
    
    
* **Attaching arbitrary data to cards**

The `data` property of `AbstractCard` allows the programmer to attach arbitrary data to any `Card` or even a stack of cards, since `CardStack extends AbstractCard`.
This attached data can be set and retrived in the following fashion:

```java
Integer myData = 42;
card.setData(myData);
//...
Integer theData = (Integer) card.getData();
// theData == 42
```

* **Setting custom card backgrounds**

If you want to provide a custom `Drawable` for the `Card`'s background, then use the `setBackgroundResource()` method:
```java
card.setBackgroundResource(R.drawable.custom_card_bg);
```


## Model-View-Controller implementation
The `CardModel` is essentially a copy of `AbstractCard`, except that it is concrete, Serializable, and specifies a target type that should be used when put through `CardFactory.createCard(CardModel)`.
`CardFactory` defines a single, static function, `createCard(CardModel)`, which uses the Reflection API to inflate a `CardModel` into a descendant of `AbstractCard` as specified by `model.cardClass`.
You can check-out an example at FLamparski's [FLamparski/areabase](Areabase), namely in the [SummaryFragment class](https://github.com/FLamparski/areabase/blob/master/Areabase/src/lamparski/areabase/SummaryFragment.java).

**Example:**

```java
CardModel model = new CardModel("This is the card's description", "This is the card's title", BasicCard.class);
BasicCard card = (BasicCard) CardFactory.createCard(model); // This cast is safe
mCardUI.addCard(card);
```

Special thanks to [https://github.com/FLamparski](FLamparski) for the MVC implementation and data attachement/background modification commits.
    
    
## CardsUI Generator
I'm working on a supplementary example app for the CardsUI library. This app includes a little daemon to generate cards and set their parameters with a nice animated gui.
Ultimately, the aim of this app will be to generate a zip file containing the code & resources needed for the cards "activity" the user generated in the app.
Considering that the cards layouts need to be on the app side, and not on the library side, this could be useful.

![screenshot1](http://imageshack.us/a/img837/1365/cardsgen1.png) ![screenshot2](http://imageshack.us/a/img708/8929/cardsgen2.png) ![screenshot3](http://imageshack.us/a/img90/7456/cardsgen3.png) ![screenshot4](http://imageshack.us/a/img109/9287/cardsgen4.png) ![screenshot5](http://imageshack.us/a/img209/8982/cardsgen5.png) ![screenshot6](http://imageshack.us/a/img515/4987/cardsgen6.png)

