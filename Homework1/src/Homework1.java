import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ForkJoinTask;

/**
 * @author Mirco Valentini 1612930
 *
 */
public class Homework1
{
	private static final int DIM = 9;
	private static final String CELLA_VUOTA = ".";
    private int soluzioni, celleVuote;
    private BigInteger spazioSoluzioni = BigInteger.valueOf(1);
    private String griglia [][]=new String[DIM][DIM];
    
    /**
     * La classe rappresenta un'istanza di sudoku che verra' risolto attraverso un algoritmo sequenziale. Gli
     * oggetti verranno costruiti a partire da una stringa che conterra' il percorso del file contenente la
     * griglia del sudoku. Si presuppone che le istanze della griglia contengano "." ove ci siano celle vuote.
     * 
     * @param percorsoFile: stringa contentente il percorso del file con la griglia da risolvere
     */
    public Homework1(String percorsoFile)
    {
    		int riga=0;
    		try
    		{
    			Scanner f = new Scanner(new FileReader(percorsoFile));
    			while(f.hasNextLine())
    			{
    				String t = f.nextLine();
    				for(int i=0; i<DIM; i++)
    				{
    					if(t.charAt(i)=='.')
    						celleVuote++;
    					griglia[riga][i] = t.split("")[i];
    				}
    				riga++;
    			}
    			f.close();
    		}
    		catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    }
    
    /**
     * Il metodo calcola lo spazio delle soluzioni associato alla matrice passata come parametro calcolandosi
     * volta per volta tutti i possibili candidati nella cella esaminata e moltiplicandoli al totale; si 
     * avvale di due metodi di comodo "controllaBox" e "controllaValore" per verificare la validita' del 
     * valore esaminato.
     * 
     * @param matriceSudoku: matrice da esaminare
     */
    private void spazioSoluzioni(String[][] matriceSudoku)
    {
        Integer candidati=0;       
        for(int riga=0;riga<9;riga++)
        {
            for(int colonna=0;colonna<9;colonna++)
            {
                if(matriceSudoku[riga][colonna].equals(CELLA_VUOTA))
                {
                    for(Integer i=1; i<=9; i++)
                    {
                    		if(controllaBox(riga, colonna, i.toString(), matriceSudoku))
                    			continue;
                    		if(controllaValore(riga, colonna, i.toString(), matriceSudoku))
                    			continue;
                    		candidati++;
                    }
                    spazioSoluzioni=spazioSoluzioni.multiply(BigInteger.valueOf(candidati));
                }
                candidati=0;
            }
        }
    }
   
    /**
     * Il metodo crea una nuova matrice a partire dalla matrice passata in input inserendo in posizione
     * "row"-"col" il valore "num" e ritorna la nuova matrice in output.
     * 
     * @param matriceCopia: matrice originale da copiare
     * @param row: riga in cui inserire num
     * @param col: colonna in cui inserire num
     * @param num: valore da inserire in row-col
     * @return: nuova matrice
     */
    private String[][] creaMatrice(String matriceCopia[][], int row, int col, String num)
    {
    		String matrice [][]=new String[DIM][DIM];
        for(int riga=0;riga<9;riga++)          
            for(int colonna=0;colonna<9;colonna++)      
                matrice[riga][colonna]=matriceCopia[riga][colonna]; 
        matrice[row][col] = num; 
        return matrice;
    }

	/**
	 * Il metodo controlla la validita' del parametro "num" in posizione "riga"-"col" nell'intorno 3x3 della
	 * matrice associata. Il valore di ritorno booleano ci indichera' la presenza o non nell'intorno descritto.
	 * 
	 * @param riga: riga del parametro num
	 * @param col: colonna del parametro num
	 * @param num: valore da controllare
	 * @param matrice: matrice associata
	 * @return: booleano per la presenza o assenza del valore nell'intorno
	 */
	private boolean controllaBox(int riga, int col, String num, String matrice[][])
	{
		boolean trovato = false;
		for(int rigaIntorno = ((riga)/3)*3; rigaIntorno<(((riga)/3)*3)+3 && trovato==false; rigaIntorno++)      
            for(int colonnaIntorno=((col)/3)*3; colonnaIntorno<(((col)/3)*3)+3 &&trovato==false; colonnaIntorno++)          
                if(String.valueOf(num).equals(matrice[rigaIntorno][colonnaIntorno]))             
                    return true;
		return false;
	}
	
	/**
	 * Il metodo controlla la validita' del parametro "num" in posizione "riga"-"col" della matrice
	 * associata lungo tutta la riga "riga" e colonna "col". Il valore di ritorno booleano ci indichera' la
	 * presenza o non nelle coordinate descritte.
	 * 
	 * @param riga: riga del parametro num
	 * @param col: colonna del parametro num
	 * @param num: valore da controllare
	 * @param matrice: matrice associata
	 * @return: booleano per la presenza o assenza del valore nell'intorno
	 */
	private boolean controllaValore(int riga, int col, String num, String matrice[][])
	{
		boolean trovato = false;
		for(int cd=0;cd<9 && trovato==false;cd++)
            if(String.valueOf(num).equals( matrice[cd][col]) || String.valueOf(num).equals( matrice[riga][cd] )  )
                return true;
		return false;
	}
	   
    /**
     * Il metodo risolve attraverso un algoritmo sequenziale la matrice del sudoku passata come parametro. 
     * L'algoritmo controlla per ogni riga e colonna la validita' dell'inserimento dei possibili valori da
     * 1 a 9 nella cella esaminata; se il controllo produrra' esito negativo il valore non sara' presente
     * negli intorni esaminati e verra' inserito come candidato ad una possibile soluzione della griglia, 
     * bloccando eventuali altre soluzioni attraverso il valore di controllo "flag" per evitare di trovare
     * soluzioni equivalenti. Data la validita' delle soluzioni ad ogni passo ricorsivo dell'algoritmo siamo
     * certi che, una volta riempite tutte le celle vuote all'interno della griglia, il risultato sara'
     * una soluzione valida.
     * 
     * @param matriceSudoku: griglia da risolvere
     */
    private void risolviSudoku(String[][] matriceSudoku)
    {
        boolean flag=false;
        int sudokuCompleto=0;
        for(int riga=0; riga<9 && flag==false; riga++) 
        {
            for(int colonna=0; colonna<9 && flag==false; colonna++) 
            {
                if(!matriceSudoku[riga][colonna].equals(CELLA_VUOTA))
                    sudokuCompleto++;
                else
                {
                	for (Integer i=1; i<=9; i++)
                    {
                        boolean presente=controllaValore(riga, colonna, i.toString(), matriceSudoku) ||
                        		controllaBox(riga, colonna, i.toString(), matriceSudoku);                   
                        if (presente==false) 
                        {
                            String matriceAppoggio[][] = creaMatrice(matriceSudoku, riga, colonna, i.toString());
                            risolviSudoku(matriceAppoggio);
                        }
                    }         	
                    flag=true;
                }
            }
        }
        if(sudokuCompleto==DIM*DIM)
            soluzioni++; 
    }
    
    /**
     * Il metodo risolve attraverso un algoritmo parallelo la matrice del sudoku passata come parametro. 
     * L'algoritmo controlla per ogni riga e colonna la validita' dell'inserimento dei possibili valori da
     * 1 a 9 nella cella esaminata; se il controllo produrra' esito negativo il valore non sara' presente
     * negli intorni esaminati e verra' inserito come candidato ad una possibile soluzione della griglia, 
     * bloccando eventuali altre soluzioni attraverso il valore di controllo "flag" per evitare di trovare
     * soluzioni equivalenti. Data la validita' delle soluzioni ad ogni passo ricorsivo dell'algoritmo siamo
     * certi che, una volta riempite tutte le celle vuote all'interno della griglia, il risultato sara'
     * una soluzione valida. 
     * 
     * @param matriceSudoku: griglia da risolvere
     */
    private void risolviSudokuParallelo(String[][] matriceSudoku)
    {
        List<ForkJoinTask> workItems = new ArrayList<>(); 
        boolean flag=false;
        int sudokuCompleto=0;
        for(int riga=0; riga<9 && flag==false; riga++) 
        {
            for(int colonna=0; colonna<9 && flag==false; colonna++) 
            {
                if(!matriceSudoku[riga][colonna].equals(CELLA_VUOTA))
                    sudokuCompleto++;
                else
                {
                	for (Integer i=1 ; i<=9 ; i++)
                    {
                        boolean presente=controllaValore(riga, colonna, i.toString(), matriceSudoku) ||
                        		controllaBox(riga, colonna, i.toString(), matriceSudoku);                   
                        if (presente==false) 
                        {
                            String matriceAppoggio[][] = creaMatrice(matriceSudoku, riga, colonna, i.toString());
                            workItems.add(ForkJoinTask.adapt(() -> risolviSudoku(matriceAppoggio)));
                        }
                    }         	
                    flag=true;
                    ForkJoinTask.invokeAll(workItems);
                }
            }
        }
        if(sudokuCompleto==DIM*DIM)
            soluzioni++; 
    }
	
    public static void main(String[] args) throws IOException
    {
    		Homework1 p = new Homework1(args[0]);
	        System.out.println("Sto risolvendo il sudoku!\n");
            p.spazioSoluzioni(p.griglia);
            for(int i=0; i<9; i++)
            {
            		for(int j=0; j<9; j++)
            			System.out.print(p.griglia[i][j]);
            		System.out.println();
            }
            
            double inizio = System.currentTimeMillis();
            p.risolviSudoku(p.griglia);
            double totaleSequenziale = System.currentTimeMillis()-inizio;
            
            inizio = System.currentTimeMillis();
            p.risolviSudokuParallelo(p.griglia);
            double totaleParallelo = System.currentTimeMillis()-inizio;
            
            System.out.println("\nLa dimensione dello spazio delle soluzioni e': "+p.spazioSoluzioni);
            System.out.println("Il fattore di riempimento della matrice di input e':" 
            						+ (100-(p.celleVuote*100)/81) + "%");
            System.out.println("Soluzioni legali sudoku: "+p.soluzioni/2);
            System.out.println("Il tempo di esecuzione sequenziale e': "+(totaleSequenziale/1000.0)+" secondi");
            System.out.println("Il tempo di esecuzione parallelo e': "+(totaleParallelo/1000.0)+" secondi");
            System.out.println("Lo speedup e' pari a: "+(totaleSequenziale/1000.0)/(totaleParallelo/1000.0)
            						+" secondi");
        }


}
