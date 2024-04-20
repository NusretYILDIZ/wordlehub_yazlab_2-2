package com.yildizsoft.wordlehub.game.online.gameplay;

import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.yildizsoft.wordlehub.R;
import com.yildizsoft.wordlehub.client.PlayerInfo;
import com.yildizsoft.wordlehub.client.PlayerLobby;
import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.dialog.InfoDialog;

import java.util.ArrayList;
import java.util.List;

public class EnterWordActivity extends AppCompatActivity
{
    List<Character> word = new ArrayList<>();
    int wordLength = 0;
    List<TextView> letterViews = new ArrayList<>();
    EnterWordActivity enterWordActivity;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_word);
        
        enterWordActivity = this;
        
        PlayerInfo player = WordleClient.GetCurrentPlayer();
        if(player != null)
        {
            LinearLayout wordInput = findViewById(R.id.wordInput);
            int layout;
            
            if(player.getLobby() == PlayerLobby.NO_CONST_4_LETTER || player.getLobby() == PlayerLobby.WITH_CONST_4_LETTER)
            {
                layout = R.layout.word_container_4_letters;
                wordLength = 4;
            }
            
            else if(player.getLobby() == PlayerLobby.NO_CONST_5_LETTER || player.getLobby() == PlayerLobby.WITH_CONST_5_LETTER)
            {
                layout = R.layout.word_container_5_letters;
                wordLength = 5;
            }
            
            else if(player.getLobby() == PlayerLobby.NO_CONST_6_LETTER || player.getLobby() == PlayerLobby.WITH_CONST_6_LETTER)
            {
                layout = R.layout.word_container_6_letters;
                wordLength = 6;
            }
            
            else //if(player.getLobby() == PlayerLobby.NO_CONST_7_LETTER || player.getLobby() == PlayerLobby.WITH_CONST_7_LETTER)
            {
                layout = R.layout.word_container_7_letters;
                wordLength = 7;
            }
            
            getLayoutInflater().inflate(layout, wordInput);
            
            letterViews.add(findViewById(R.id.letter1));
            letterViews.add(findViewById(R.id.letter2));
            letterViews.add(findViewById(R.id.letter3));
            letterViews.add(findViewById(R.id.letter4));
            
            if(wordLength >= 5) letterViews.add(findViewById(R.id.letter5));
            if(wordLength >= 6) letterViews.add(findViewById(R.id.letter6));
            if(wordLength >= 7) letterViews.add(findViewById(R.id.letter7));
        }
        
        
        // ERTYUIOPĞÜ
        
        LinearLayout letterE = findViewById(R.id.letterE);
        letterE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('E');
            }
        });
        
        LinearLayout letterR = findViewById(R.id.letterR);
        letterR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('R');
            }
        });
        
        LinearLayout letterT = findViewById(R.id.letterT);
        letterT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('T');
            }
        });
        
        LinearLayout letterY = findViewById(R.id.letterY);
        letterY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('Y');
            }
        });
        
        LinearLayout letterU = findViewById(R.id.letterU);
        letterU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('U');
            }
        });
        
        LinearLayout letterI = findViewById(R.id.letterI);
        letterI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('I');
            }
        });
        
        LinearLayout letterO = findViewById(R.id.letterO);
        letterO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('O');
            }
        });
        
        LinearLayout letterP = findViewById(R.id.letterP);
        letterP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('P');
            }
        });
        
        LinearLayout letterGG = findViewById(R.id.letterGG);
        letterGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('Ğ');
            }
        });
        
        LinearLayout letterUU = findViewById(R.id.letterUU);
        letterUU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('Ü');
            }
        });
        
        
        // ASDFGHJKLŞİ
        
        LinearLayout letterA = findViewById(R.id.letterA);
        letterA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('A');
            }
        });
        
        LinearLayout letterS = findViewById(R.id.letterS);
        letterS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('S');
            }
        });
        
        LinearLayout letterD = findViewById(R.id.letterD);
        letterD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('D');
            }
        });
        
        LinearLayout letterF = findViewById(R.id.letterF);
        letterF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('F');
            }
        });
        
        LinearLayout letterG = findViewById(R.id.letterG);
        letterG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('G');
            }
        });
        
        LinearLayout letterH = findViewById(R.id.letterH);
        letterH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('H');
            }
        });
        
        LinearLayout letterJ = findViewById(R.id.letterJ);
        letterJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('J');
            }
        });
        
        LinearLayout letterK = findViewById(R.id.letterK);
        letterK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('K');
            }
        });
        
        LinearLayout letterL = findViewById(R.id.letterL);
        letterL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('L');
            }
        });
        
        LinearLayout letterSS = findViewById(R.id.letterSS);
        letterSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('Ş');
            }
        });
        
        LinearLayout letterII = findViewById(R.id.letterII);
        letterII.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('İ');
            }
        });
        
        
        // ZCVBNMÖÇ
        
        LinearLayout backspaceButton = findViewById(R.id.backspaceButton);
        backspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DeleteLetter();
            }
        });
        
        LinearLayout letterZ = findViewById(R.id.letterZ);
        letterZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('Z');
            }
        });
        
        LinearLayout letterC = findViewById(R.id.letterC);
        letterC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('C');
            }
        });
        
        LinearLayout letterV = findViewById(R.id.letterV);
        letterV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('V');
            }
        });
        
        LinearLayout letterB = findViewById(R.id.letterB);
        letterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('B');
            }
        });
        
        LinearLayout letterN = findViewById(R.id.letterN);
        letterN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('N');
            }
        });
        
        LinearLayout letterM = findViewById(R.id.letterM);
        letterM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('M');
            }
        });
        
        LinearLayout letterOO = findViewById(R.id.letterOO);
        letterOO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('Ö');
            }
        });
        
        LinearLayout letterCC = findViewById(R.id.letterCC);
        letterCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EnterLetter('Ç');
            }
        });
        
        /*LinearLayout enterButton = findViewById(R.id.enterButton);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
            
            }
        });*/
        
        Button verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new Thread(new VerifyEnterWordRunnable(enterWordActivity, GetWord())).start();
            }
        });
    }
    
    public void InvalidWord()
    {
        new InfoDialog(this, "Girdiğiniz kelime geçerli bir kelime değil.").Show();
        ClearWord();
    }
    
    public String GetWord()
    {
        StringBuilder builder = new StringBuilder();
        
        for(Character c : word) builder.append(c);
        
        return builder.toString();
    }
    
    public void EnterLetter(Character letter)
    {
        if(word.size() < wordLength)
        {
            word.add(letter);
            UpdateWord();
        }
    }
    
    public void DeleteLetter()
    {
        if(!word.isEmpty())
        {
            word.remove(word.size() - 1);
            UpdateWord();
        }
    }
    
    public void ClearWord()
    {
        word.clear();
        UpdateWord();
    }
    
    public void UpdateWord()
    {
        for(int i = 0; i < wordLength; i++)
        {
            if(i < word.size())
            {
                letterViews.get(i).setText(GetChar(word.get(i)));
            }
            else
            {
                letterViews.get(i).setText(R.string.letter_empty);
            }
        }
    }
    
    public int GetChar(Character character)
    {
        switch(character)
        {
        case 'E': return R.string.letter_e;
        case 'R': return R.string.letter_r;
        case 'T': return R.string.letter_t;
        case 'Y': return R.string.letter_y;
        case 'U': return R.string.letter_u;
        case 'I': return R.string.letter_i;
        case 'O': return R.string.letter_o;
        case 'P': return R.string.letter_p;
        case 'Ğ': return R.string.letter_gg;
        case 'Ü': return R.string.letter_uu;
        
        case 'A': return R.string.letter_a;
        case 'S': return R.string.letter_s;
        case 'D': return R.string.letter_d;
        case 'F': return R.string.letter_f;
        case 'G': return R.string.letter_g;
        case 'H': return R.string.letter_h;
        case 'J': return R.string.letter_j;
        case 'K': return R.string.letter_k;
        case 'L': return R.string.letter_l;
        case 'Ş': return R.string.letter_ss;
        case 'İ': return R.string.letter_ii;
        
        case 'Z': return R.string.letter_z;
        case 'C': return R.string.letter_c;
        case 'V': return R.string.letter_v;
        case 'B': return R.string.letter_b;
        case 'N': return R.string.letter_n;
        case 'M': return R.string.letter_m;
        case 'Ö': return R.string.letter_oo;
        case 'Ç': return R.string.letter_cc;
        
        default: return R.string.letter_empty;
        }
    }
}