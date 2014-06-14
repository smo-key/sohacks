package com.androguide.cardsui.generator;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.cardsui.example.R;
import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.ColorPicker.OnColorChangedListener;

public class MainActivity extends SherlockActivity {

	private CardUI mCardView;
	private ActionMode mActionMode;
	private ColorPicker titleColorPicker, stripeColorPicker;
	private CheckBox overflow, clickable, addToStack;
	private Button createCard;
	private Spinner cardType;
	private EditText cardTitle, cardDesc;
	private View divider;
	private TextView colorTitle, colorStripe;
	private ImageView expandCollapse;

	private LinearLayout clickableLayout, addCardLayout, colorPickersLayout,
			checkBoxesLayout;

	private Boolean expanded = false, expanding = false, isGooglePlay = false,
			isGoogleNow = false, isStack = false, doAddToStack = false;

	private int duration = 900;
	private String stripeColor, titleColor;
	private AlphaAnimation fadeIn, fadeOut;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init CardView
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);

		titleColorPicker = (ColorPicker) findViewById(R.id.titlePicker);

		stripeColorPicker = (ColorPicker) findViewById(R.id.stripePicker);
		clickable = (CheckBox) findViewById(R.id.checkClickable);
		addToStack = (CheckBox) findViewById(R.id.checkToStack);
		overflow = (CheckBox) findViewById(R.id.checkOverflow);
		createCard = (Button) findViewById(R.id.createCard);
		cardTitle = (EditText) findViewById(R.id.editTitle);
		cardDesc = (EditText) findViewById(R.id.editDesc);
		cardType = (Spinner) findViewById(R.id.cardType);
		divider = (View) findViewById(R.id.divider);
		expandCollapse = (ImageView) findViewById(R.id.expandCollapse);
		addCardLayout = (LinearLayout) findViewById(R.id.newCardLayout);
		clickableLayout = (LinearLayout) findViewById(R.id.clickableLayout);
		colorPickersLayout = (LinearLayout) findViewById(R.id.colorPickersLayout);
		checkBoxesLayout = (LinearLayout) findViewById(R.id.checkboxesLayout);
		colorTitle = (TextView) findViewById(R.id.colorTitle);
		colorStripe = (TextView) findViewById(R.id.colorStripe);

		addCardLayout.setVisibility(View.GONE);

		cardType.setVisibility(View.GONE);
		cardTitle.setVisibility(View.GONE);
		cardDesc.setVisibility(View.GONE);
		colorPickersLayout.setVisibility(View.GONE);
		divider.setVisibility(View.GONE);
		checkBoxesLayout.setVisibility(View.GONE);
		createCard.setVisibility(View.GONE);

		handleButton();
		handleSpinner();
		handleColorPickers();
		handleExpandCollapseClick();
		generateInitialCards();
		mCardView.refresh();
	}

	private void generateNewCard() {

		if (isGooglePlay == true) {
			
			MyPlayCard customCard = new MyPlayCard(cardTitle.getText()
					.toString(), cardDesc.getText().toString(), stripeColor,
					titleColor, overflow.isChecked(), clickable.isChecked());

			if ((doAddToStack = addToStack.isChecked()) == true) {

				mCardView.addCardToLastStack(customCard);
				mCardView.refresh();

			} else {

				mCardView.addCard(customCard);
				mCardView.refresh();
			}

		} else if (isGoogleNow == true) {

			MyCard customCard = new MyCard(cardTitle.getText().toString(),
					cardDesc.getText().toString());

			if ((doAddToStack = addToStack.isChecked()) == true) {
				
				mCardView.addCardToLastStack(customCard);
				mCardView.refresh();
				
			} else {
				
				mCardView.addCard(customCard);
				mCardView.refresh();
			}
			
		} else if (isStack == true) {
			
			CardStack stack = new CardStack();
			stack.setTitle(cardTitle.getText().toString());
			
		}
		
	}

	private void handleButton() {

		createCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				generateNewCard();
			}
		});
	}

	private void handleSpinner() {

		cardType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {

				if (pos == 0) {

					if (isGooglePlay == true)
						animateGooglePlay();
					if (isGoogleNow == true || isStack == true)
						animateExpand();

				} else if (pos == 1) {

					animateGoogleNow();

				} else if (pos == 2) {

				} else if (pos == 3) {

					animateStack();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	private void handleExpandCollapseClick() {

		clickableLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// Make sure other clicks don't interrupt the animation
				if (expanding == false) {

					if (expanded == false) {
						expandCollapse
								.setImageResource(R.drawable.ic_action_collapse);
						animateExpand();
						expanding = true;

					} else if (expanded == true) {
						expandCollapse
								.setImageResource(R.drawable.ic_action_expand);
						animateCollapse();
						expanding = true;

					}
				}
			}
		});

	}

	private void handleColorPickers() {

		titleColorPicker
				.setOnColorChangedListener(new OnColorChangedListener() {
					@Override
					public void onColorChanged(int color) {
						colorTitle.setTextColor(titleColorPicker.getColor());
						titleColor = String.format("#%06X",
								(0xFFFFFF & titleColorPicker.getColor()));
					}
				});

		stripeColorPicker
				.setOnColorChangedListener(new OnColorChangedListener() {
					@Override
					public void onColorChanged(int color) {
						colorStripe.setTextColor(stripeColorPicker.getColor());
						stripeColor = String.format("#%06X",
								(0xFFFFFF & stripeColorPicker.getColor()));
					}
				});
	}

	private void generateInitialCards() {

		CardStack stackPlay = new CardStack();
		stackPlay.setTitle("CARDS UI GENERATOR");
		mCardView.addStack(stackPlay);
		stackPlay.setColor("#33b6ea");

		mCardView.addCard(new MyPlayCard("Google Play",
				"This card mimics the new Google play cards look\n\n",
				"#33b6ea", "#33b6ea", true, false));

		mCardView
				.addCardToLastStack(new MyPlayCard(
						"Menu Overflow",
						"The PlayCards allow you to easily set a menu overflow on your card.\nYou can also declare the left stripe's color in a String, like \"#33B5E5\" for the holo blue color, same for the title color.",
						"#e00707", "#e00707", false, true));

		mCardView
				.addCard(new MyPlayCard(
						"Different Colors for Title & Stripe",
						"You can set any color for the title and any other color for the left stripe",
						"#f2a400", "#9d36d0", false, false));

		mCardView
				.addCardToLastStack(new MyPlayCard(
						"Set Clickable or Not",
						"You can easily implement an onClickListener on any card, but the last boolean parameter of the PlayCards allow you to toggle the clickable background.",
						"#4ac925", "#222222", true, true));

		MyPlayCard card = new MyPlayCard(
				"Swipe & Bring Cards Back!",
				"You can easily implement a Contextual Action Bar which shows-up when a card is swiped, allowing to bring it back. Swipe Me to try!",
				"#9a32ce", "#222222", true, true);
		card.setOnCardSwipedListener(new Card.OnCardSwiped() {

			@Override
			public void onCardSwiped(Card card, View layout) {

				if (mActionMode != null) {
					// Do nothing, the contextual ActionBar is already shown
				} else
					mActionMode = startActionMode(mActionModeCallback);

			}
		});
		mCardView.addCard(card);
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode,
				com.actionbarsherlock.view.Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.activity_main, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode,
				com.actionbarsherlock.view.Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {

			case R.id.refresh:
				mCardView.clearCards();
				generateInitialCards();
				mCardView.refresh();
				mode.finish();
				return true;

			default:
				return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}

	};

	private void animateExpand() {

		fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(duration);
		fadeIn.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation arg0) {
			}

			public void onAnimationRepeat(Animation arg0) {
			}

			public void onAnimationEnd(Animation arg0) {
				expanding = false;
				expanded = true;
				isGooglePlay = true;
			}
		});

		addCardLayout.setVisibility(View.VISIBLE);
		addCardLayout.startAnimation(fadeIn);
		cardType.setVisibility(View.VISIBLE);
		cardTitle.setVisibility(View.VISIBLE);
		cardDesc.setVisibility(View.VISIBLE);
		colorPickersLayout.setVisibility(View.VISIBLE);
		divider.setVisibility(View.VISIBLE);
		checkBoxesLayout.setVisibility(View.VISIBLE);
		createCard.setVisibility(View.VISIBLE);
		
		isStack = false;
		isGoogleNow = false;
		isGooglePlay = true;
	}

	private void animateGoogleNow() {

		fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(500);
		fadeOut.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation arg0) {
			}

			public void onAnimationRepeat(Animation arg0) {
			}

			public void onAnimationEnd(Animation arg0) {

				colorPickersLayout.setVisibility(View.GONE);
				divider.setVisibility(View.GONE);
			}
		});

		if (isGooglePlay == true) {
			colorPickersLayout.startAnimation(fadeOut);
			divider.startAnimation(fadeOut);

		} else if (isStack == true) {

			fadeIn = new AlphaAnimation(0.0f, 1.0f);
			fadeIn.setDuration(500);
			fadeIn.setAnimationListener(new AnimationListener() {

				public void onAnimationStart(Animation arg0) {
				}

				public void onAnimationRepeat(Animation arg0) {
				}

				public void onAnimationEnd(Animation arg0) {
				}
			});

			cardDesc.setVisibility(View.VISIBLE);
			cardDesc.startAnimation(fadeIn);

			colorPickersLayout.setVisibility(View.GONE);
			divider.setVisibility(View.GONE);
		}

		cardDesc.setVisibility(View.VISIBLE);

		isGoogleNow = true;
		isGooglePlay = false;
		isStack = false;
		cardTitle.setHint(getResources().getString(R.string.card_title));
	}

	private void animateGooglePlay() {

		fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(duration);
		fadeIn.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation arg0) {
			}

			public void onAnimationRepeat(Animation arg0) {
			}

			public void onAnimationEnd(Animation arg0) {

				cardTitle
						.setHint(getResources().getString(R.string.card_title));
			}
		});

		colorPickersLayout.setVisibility(View.VISIBLE);
		divider.setVisibility(View.VISIBLE);
		cardDesc.setVisibility(View.VISIBLE);
		colorPickersLayout.startAnimation(fadeIn);
		divider.startAnimation(fadeIn);

		isGoogleNow = false;
		isGooglePlay = true;
		isStack = false;
	}

	private void animateStack() {

		fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(500);
		fadeOut.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation arg0) {
			}

			public void onAnimationRepeat(Animation arg0) {
			}

			public void onAnimationEnd(Animation arg0) {

				if (isGooglePlay == true) {
					colorPickersLayout.setVisibility(View.GONE);
					checkBoxesLayout.setVisibility(View.GONE);
					divider.setVisibility(View.GONE);
				}

				cardDesc.setVisibility(View.GONE);
				cardTitle.setHint(getResources()
						.getString(R.string.stack_title));
				
				isStack = true;
				isGoogleNow = false;
				isGooglePlay = false;
			}
		});

		if (isGooglePlay == true) {
			colorPickersLayout.startAnimation(fadeOut);
			checkBoxesLayout.startAnimation(fadeOut);
			divider.startAnimation(fadeOut);
		} else if (isGoogleNow == true) {
			colorPickersLayout.setVisibility(View.GONE);
			checkBoxesLayout.setVisibility(View.GONE);
			divider.setVisibility(View.GONE);
		}
		cardDesc.startAnimation(fadeOut);

		
	}

	private void animateCollapse() {

		fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(1000);
		fadeOut.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation arg0) {
			}

			public void onAnimationRepeat(Animation arg0) {
			}

			public void onAnimationEnd(Animation arg0) {

				expanding = false;
				expanded = false;
			}
		});

		addCardLayout.startAnimation(fadeOut);
		addCardLayout.setVisibility(View.GONE);
		cardType.setVisibility(View.GONE);
		cardTitle.setVisibility(View.GONE);
		cardDesc.setVisibility(View.GONE);
		colorPickersLayout.setVisibility(View.GONE);
		divider.setVisibility(View.GONE);
		checkBoxesLayout.setVisibility(View.GONE);
		createCard.setVisibility(View.GONE);
		
		isStack = false;
		isGoogleNow = false;
		isGooglePlay = false;
	}
}
