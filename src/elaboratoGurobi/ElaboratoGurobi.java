package elaboratoGurobi;


/*L’azienda Lambdamatic è attiva sul territorio con m magazzini ed ha stipulato un contratto di fornitura esclusiva
di caffé in grani con n clienti. Ogni giorno, ogni cliente i deve essere rifornito di ri Kg di caffé. Per soddisfare le
domande, l’azienda dovrà procedere all’invio della merce stoccata nei suoi m diversi magazzini. La capacità massima
di stoccaggio di ciascun magazzino j è fissa ed è pari a sj Kg. La distanza da ciascun magazzino j a ciascun punto
vendita i è nota ed è pari a dji Km. Per trasportare 1 Kg di merce per 1 Km, l’azienda stima un costo pari a c euro.
Lambdamatic vuole soddisfare tutte le domande minimizzando il costo complessivo di trasporto, assicurandosi altresı̀
che ciascun punto vendita i sia servito solo da magazzini posizionati nel raggio di k Km.*/

import gurobi.*;
import gurobi.GRB.DoubleAttr;
import gurobi.GRB.IntParam;
import gurobi.GRB.StringAttr;

public class ElaboratoGurobi
{
	static int m = 25; //numero magazzini
	static int n = 51; //numero clienti
	static int[][] d_ij= //distanza in Km da ciascun magazzino i a ciascun punto vendita j
		{
			{13,40,11,35,43,11,16,11,20,25,26,24,40,13,19,2,24,29,21,24,11,28,36,35,21,1,13,4,20,15,38,33,16,10,32,41,23,34,10,16,18,32,27,19,19,9,25,39,10,20,14},
			{15,33,15,30,27,14,10,33,35,42,37,45,48,39,8,23,6,4,31,34,29,30,34,41,42,27,19,30,6,11,21,11,17,18,20,48,21,39,36,18,34,13,26,41,44,25,15,46,22,40,14},
			{30,12,26,8,21,25,22,25,17,22,12,29,20,36,28,28,21,26,10,11,19,2,8,14,27,28,35,28,22,22,19,23,14,30,12,21,6,11,35,12,16,20,1,26,34,35,39,18,34,23,18,},
			{20,42,17,37,47,18,22,6,18,22,25,19,38,7,25,9,30,35,21,23,10,30,37,34,16,6,19,2,27,21,42,39,20,16,36,40,26,33,6,20,16,38,29,14,12,13,31,38,16,16,19},
			{33,10,31,10,7,29,24,37,31,36,27,43,32,47,28,36,18,20,24,25,31,16,15,26,41,37,38,38,21,24,5,14,20,35,4,32,14,23,46,20,30,11,13,40,47,41,39,29,39,37,23},
			{28,35,24,30,43,24,26,5,6,9,14,8,26,15,31,19,32,38,13,13,7,22,29,23,6,16,29,13,30,25,40,39,20,26,33,28,22,23,17,19,6,38,23,4,11,25,40,27,27,4,22},
			{32,19,28,15,29,27,25,19,9,13,4,21,15,31,32,27,27,33,4,3,15,6,12,9,19,26,35,25,27,25,27,30,16,31,20,16,12,8,31,15,10,28,9,19,28,34,43,14,34,16,20},
			{32,30,28,26,40,27,28,12,2,4,8,10,20,22,34,24,33,39,9,7,10,17,24,17,8,22,34,19,31,27,37,38,20,30,30,21,20,17,24,19,4,36,19,8,17,30,44,20,31,4,23},
			{36,16,34,17,3,33,27,43,38,44,34,50,39,53,30,40,20,20,31,33,37,23,21,33,48,42,41,44,24,28,3,12,25,39,9,38,20,30,51,25,37,11,20,47,54,45,40,36,42,44,27},
			{8,48,12,44,45,13,17,31,38,45,43,44,57,31,12,17,22,23,37,40,29,40,47,50,41,20,6,24,19,17,39,30,25,10,36,57,32,48,26,26,36,31,38,39,38,13,6,55,10,40,20},
			{14,46,13,41,48,15,20,15,25,30,32,27,46,12,21,6,28,32,27,30,17,35,42,41,25,5,12,7,24,19,43,37,22,10,38,47,29,40,8,22,24,37,34,23,20,5,24,45,8,25,19},
			{8,32,4,28,32,3,3,20,24,31,27,33,40,26,8,11,11,16,21,24,16,24,30,34,30,14,12,16,7,2,26,20,8,9,22,41,16,32,23,9,22,20,21,28,31,15,18,38,12,28,4},
			{17,36,14,31,41,14,17,6,14,20,21,19,35,13,21,8,24,30,16,18,5,24,31,30,16,6,18,4,21,16,36,33,14,14,30,36,20,29,12,14,12,32,23,14,16,14,29,34,15,15,14},
			{30,41,26,36,48,27,30,6,12,13,20,7,31,11,34,19,36,42,18,18,11,28,34,28,5,16,30,13,34,28,45,44,24,27,38,32,28,28,14,24,11,42,29,3,6,25,42,31,27,7,25},
			{10,34,6,29,35,6,9,14,19,26,24,27,38,20,13,6,17,22,18,21,11,23,30,32,24,8,13,10,13,8,30,25,9,9,25,39,16,30,18,9,17,25,21,22,25,12,22,36,11,22,6},
			{19,28,18,25,22,17,11,34,34,41,34,45,44,41,12,26,3,2,28,32,29,26,29,38,42,29,24,31,7,12,15,6,15,22,15,45,17,35,38,17,32,7,22,40,45,29,21,42,26,39,14},
			{41,9,38,12,4,36,31,43,36,41,31,48,33,54,35,43,25,27,29,30,37,20,16,28,46,44,46,45,28,32,9,19,27,42,11,32,20,25,53,27,36,17,18,45,53,48,46,30,46,42,30},
			{4,37,4,33,35,4,6,25,30,37,33,38,46,29,3,13,12,15,27,31,22,30,36,40,35,16,9,20,8,6,29,21,14,7,26,47,21,38,25,15,28,22,27,33,35,14,12,45,10,33,10},
			{35,39,32,34,48,32,33,11,10,7,16,1,25,18,39,25,39,45,17,16,14,26,32,24,1,22,36,19,37,32,45,46,27,32,38,27,28,24,21,26,11,44,28,3,11,31,47,26,33,3,29},
			{53,26,49,25,39,48,46,37,25,22,18,30,5,47,52,47,46,51,24,21,34,22,20,11,31,46,56,43,47,45,41,47,37,52,35,3,31,13,49,35,27,45,26,32,41,54,63,6,54,28,41},
			{5,41,5,37,42,7,12,19,26,33,32,32,46,20,12,5,20,24,26,29,17,31,39,40,29,8,5,12,16,11,36,29,16,2,32,47,24,38,16,17,25,30,30,27,27,5,17,44,3,28,13},
			{22,32,18,27,38,18,20,5,8,14,15,15,28,16,25,14,26,32,10,12,0,19,27,24,12,12,24,10,24,19,35,33,14,20,28,30,17,23,16,13,6,32,19,10,16,20,34,28,22,10,15},
			{24,17,20,12,20,18,15,23,18,25,16,30,27,33,21,23,14,20,11,14,17,9,14,20,28,24,28,24,15,15,17,18,7,24,10,27,0,18,32,6,17,16,6,27,34,29,32,25,28,24,11},
			{37,26,33,22,37,32,31,19,8,7,3,15,12,29,38,30,34,40,9,5,16,14,18,9,15,28,39,26,34,30,35,38,23,35,29,14,20,10,31,21,9,36,17,15,24,36,48,13,37,11,26},
			{23,23,19,18,29,18,17,14,10,17,11,21,24,25,23,19,20,26,5,8,9,10,18,18,19,18,27,18,19,16,26,26,8,22,19,25,8,16,25,7,8,24,10,18,25,25,34,23,25,16,12}
		};
	
	// richieste dei n clienti n=51
	static int[] domanda = {8000,8000,6000,13000,13000,6000,11000,13000,7000,13000,12000,10000,7000,10000,11000,10000,5000,5000,9000,7000,10000,12000,7000,6000,7000,8000,12000,7000,10000,12000,10000,5000,10000,10000,13000,10000,12000,13000,12000,6000,8000,7000,13000,13000,12000,5000,10000,6000,9000,7000,6000};
	
	static //capacità degli m magazzini m=25
	int[] produzione = {18000,24000,20000,16000,19000,23000,17000,18000,21000,23000,15000,17000,24000,25000,23000,23000,23000,17000,25000,24000,15000,22000,25000,23000,23000};
	
	static double c = 0.01;// costo trasposrto €/Km
	static int k = 12; //raggio Km
	
	static int[] alfa_j = {1600,3500,2900,1300,4600,4100,600,4400,600,4400,2400,1600,4300,1000,1400,3800,1300,2600,1600,4300,2500,1600,1000,900,3600}; //	quesito III
	static int h = 14; //Questo II capacità magazzino 14
	static int x = 3;// Quesito II magazzino 3
	static int y = 4;//Quesito II cliente 4
	
	public static void main(String[] args)
	{
		try
		{
			GRBEnv env = new GRBEnv("elaboratoGurobi.log");
			
			env.set(IntParam.Presolve, 0);
			env.set(IntParam.Method, 0);
			
			//Per non far stampare a video output relativo all'env
			env.set(GRB.IntParam.OutputFlag, 0);
			
			GRBModel model = new GRBModel(env);
			
			// Creazione delle variabili di decisione
			GRBVar[][] xij = aggiungiVariabili(model, produzione, domanda);
			
			//Creazione delle variabili binarie
			
			int[][] y = creaAusiliarie(d_ij, k);
			
			// Aggiunta della funzione obiettivo
			aggiungiFunzioneObiettivo(model, xij, d_ij, c);
			
			// Aggiunta vincoli produzione
			aggiungiVincoliProduzione(model, xij, produzione);
			
			// Aggiunta vincolo domanda
			aggiungiVincoliDomanda(model, xij, domanda, y);
			
			//Aggiunta vincolo distanze
			
			// Ottimizza il modello
			model.optimize();
			
			stampaRisposte(env, model, y, xij);
		} catch (GRBException e)
		{
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}
		
	private static int[][] creaAusiliarie(int[][] distanza, int raggio) throws GRBException {
		
		int[][] y = new int[m][n];

		// Creazione delle variabili binarie yij
		for (int i = 0; i < m; i++) {
		  for (int j = 0; j < n; j++) {
			  if(distanza[i][j]>raggio) {
				  y[i][j] =0;
			  }
			  else {
				  y[i][j] =1;
			  }
		  }
		}
	return y;
}

	private static void stampaRisposte(GRBEnv env, GRBModel model, int[][] ausiliarie, GRBVar[][] xij) {
		System.out.println("GRUPPO 15.");
		System.out.println("Componenti: Santicoli.");
		System.out.println("");
		System.out.println("QUESITO I: ");
		
		try {
			System.out.printf("funzione obiettivo = %.04f\n", model.get(GRB.DoubleAttr.ObjVal));
			System.out.println("soluzione di base ottima:");
           
			
			for(int i=0; i<m; i++) {
				for(int j=0; j<m; j++) {
					
					System.out.println("x_"+i+"_"+j + " : "+ xij[i][j].get(GRB.DoubleAttr.X));
				}
			}
            System.out.println("");
            
            // Soluzione ottima multipla
            System.out.print("Multipla: ");
            // Controllo se c'è una var non in base con CCR nullo
            boolean multipla = false;

            // Ciclo su tutte le var, guardo tra quelle non in base
            // se loro CCR è nullo

            // Per var originali
			  for (GRBVar var : model.getVars()) { 
				  if (var.get(GRB.IntAttr.VBasis) != 0) {
					  if(var.get(GRB.DoubleAttr.RC) == 0.) {
						  	multipla = true; 
						  	break; 
						  	} 
					  } 
				  } //Per var di slack 
			 
			  for (GRBConstr constr : model.getConstrs()){
				 if(constr.get(GRB.IntAttr.CBasis) != 0){
					 if (constr.get(GRB.DoubleAttr.Pi) == 0.) 
					 { 
						 multipla = true; 
						 break; 
						 }
					 }
				 }
			 
			  System.out.print(multipla ? "Sì" : "No");

            System.out.println("");
            
            // Soluzione ottima degenere
            System.out.print("Degenere: ");
            // Controllo se c'è una var in base nulla.
            boolean degenere = false;

            // Ciclo su tutte le var, guardo tra quelle in base se nulle
            // Per var originali
            for (GRBVar var : model.getVars()) {
                if (var.get(GRB.IntAttr.VBasis) == 0){
                    if (var.get(GRB.DoubleAttr.X) == 0.){
                        degenere = true;
                        break;
                    }
                }
            }
            // Per var di slack
            for (GRBConstr constr : model.getConstrs()) {
                if (constr.get(GRB.IntAttr.CBasis) == 0) {
                    if (constr.get(GRB.DoubleAttr.Slack) == 0.) {
                        degenere = true;
                        break;
                    }
                }
            }
            System.out.println(degenere ? "Sì" : "No");
            
            System.out.println("");
            
            System.out.println("QUESITO II: ");
               
            stampaVincoliInattivi(model);
            
            stampaIntervalloK();
            
            risolviDuale(model);
            
            faiAnalisiSensitivitaMagazzino(model, h);
            
            faiAnalisiSensitivitaDistanza(model);
            
            
            System.out.println("QUESITO III: ");
            
            rispondiTerzoQuesito();
            
            
            
            
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void rispondiTerzoQuesito() throws GRBException {
		// Definizione della funzione obiettivo (massimizzare il risparmio ottenibile dalla chiusura dei magazzini)
		GRBEnv env = new GRBEnv();
		
		env.set(IntParam.Presolve, 0);
		env.set(IntParam.Method, 0);
		
		//Per non far stampare a video output relativo all'env
		env.set(GRB.IntParam.OutputFlag, 0);
		
		GRBModel model = new GRBModel(env);
		
		// Creazione delle variabili di decisione
		GRBVar[][] xij = aggiungiVariabili(model, produzione, domanda);
		GRBVar[] a_i = aggiungiVariabiliAusiliarieMagazzini(model);

		//Creazione delle variabili binarie
		
		int[][] y = creaAusiliarie(d_ij, k);
		
		// Aggiunta della funzione obiettivo, devo massimizzare il guadagno
		GRBLinExpr obj = new GRBLinExpr();
        for (int i = 0; i < alfa_j.length; i++) {
            obj.addTerm(alfa_j[i], a_i[i]);
        }
        model.setObjective(obj, GRB.MAXIMIZE);
		
		// Aggiunta vincoli produzione
        aggiungiVincoliProduzioneConBinarie(model, xij, a_i);
		
		// Aggiunta vincolo domanda
		aggiungiVincoliDomanda(model, xij, domanda, y);
		
		// Ottimizza il modello
		model.optimize();
		
	     // Stampa a video del risparmio ottenibile dall'azienda
        System.out.println("Risparmio ottenibile: " + model.get(GRB.DoubleAttr.ObjVal));
        
        //Stampa della lista dei magazzini chiusi
        System.out.print("Lista magazzini chiusi = [");
        for(int i=0; i<a_i.length; i++) {
        	if(a_i[i].get(GRB.DoubleAttr.X) == 0.0) {
        		
        		System.out.print("Magazzino " + i+ ",  ");
        	}
        	
        }
        System.out.print("]");   

        
        double thresholdSpedizioni = 0.0;
        double sum=0;
        
        for(int i=0; i<m; i++) {
        	for(int j=0; j<n; j++) {
        		sum += xij[i][j].get(GRB.DoubleAttr.X);
        	}
        }
        
        thresholdSpedizioni = sum /m;
        
        System.out.println("thresholdSpedizioni " + thresholdSpedizioni);
        
        for(int i=0; i<m; i++) {
        	sum=0;
        	for(int j=0; j<n; j++) {
        		sum += xij[i][j].get(GRB.DoubleAttr.X);
        	}
        	if(sum < thresholdSpedizioni && a_i[i].get(GRB.DoubleAttr.X) == .0) {
        		System.out.println("Magazzino meno usato: "+i);
        	}
        }
        
        double relaxationOptimum = model.get(GRB.DoubleAttr.ObjVal);
        System.out.println("Relaxation optimum: " + relaxationOptimum);
        
}


	private static void aggiungiVincoliProduzioneConBinarie(GRBModel model, GRBVar[][] xij, GRBVar[] a_i) throws GRBException {
		
	
		
		for (int i = 0; i < m; i++) {
		    
		    // modifica il vincolo di capacità del magazzino j
		    GRBLinExpr expr = new GRBLinExpr();
		    for (int j = 0; j < n; j++) {
		        expr.addTerm(1.0, xij[i][j]);
		    }
		    
		   if( i < alfa_j.length) {
			   expr.addTerm(produzione[i], a_i[i]);
			   model.addConstr(expr, GRB.LESS_EQUAL, produzione[i], "vincolo_prod_i_" +i);
		   }
		   else {
			   model.addConstr(expr, GRB.LESS_EQUAL, produzione[i], "vincolo_produzione_i_"+i);
		   }
		}
		
	}

	private static GRBVar[] aggiungiVariabiliAusiliarieMagazzini(GRBModel model) throws GRBException {
		GRBVar[] xi = new GRBVar[alfa_j.length];

		for (int i = 0; i < m; i++)
		{
			
			xi[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x" + i);
			
		}
		return xi;
	}

	private static void faiAnalisiSensitivitaDistanza(GRBModel model) throws GRBException {
		
		
		int dMax=0, dMin=0;
		
		GRBVar[] originalVars = model.getVars(); 
		// Ottiene tutte le variabili del modello
		double[] originalVarValues = new double[originalVars.length];
		// Crea un array per i valori delle variabili
		for (int i = 0; i < originalVars.length; i++) {
		    originalVarValues[i] = originalVars[i].get(GRB.DoubleAttr.X); // Aggiunge il valore della variabile all'array
		}
        System.out.println("");
        
        int distanza = d_ij[x][y];
        
        //Guardo cosa succede quando incremento la distanza dxy
        try
		{
			GRBEnv envIncrement = new GRBEnv();
			
			envIncrement.set(IntParam.Presolve, 0);
			envIncrement.set(IntParam.Method, 0);
			envIncrement.set(GRB.IntParam.OutputFlag, 0);
			
			boolean basiUguali = true;
			
			int distanzaIncr = distanza;
			
			while(basiUguali) {
	
				distanzaIncr++;
				
				GRBModel modelIncrement = new GRBModel(envIncrement);
				
				// Creazione delle variabili di decisione
				GRBVar[][] xij = aggiungiVariabili(modelIncrement, produzione, domanda);
				
				// Aggiunta vincoli produzione
				aggiungiVincoliProduzione(modelIncrement, xij, produzione);
				
				
				//Creazione delle variabili binarie
				int[][] distanze= modificaDistanza(distanzaIncr);
				int[][] y = creaAusiliarie(distanze, k);
				
				// Aggiunta della funzione obiettivo
				aggiungiFunzioneObiettivo(modelIncrement, xij, distanze, c);
				
				// Aggiunta vincolo domanda
				aggiungiVincoliDomanda(modelIncrement, xij, domanda, y);
				modelIncrement.optimize();
				
				//ricavo valori delle variabili di modelIncrement
				
				// Ciclo sulle var originali
				// Ricavo tutte le variabili del modello
				GRBVar[] modelIncrementVars = modelIncrement.getVars();
				// Crea un array con i valori delle variabili
				double[] modelIncrementVarsValue = new double[modelIncrementVars.length]; 
				
				
				// Aggiunge il valore della variabile all'array
				for (int i = 0; i < modelIncrementVars.length; i++) {
					modelIncrementVarsValue[i] = modelIncrementVars[i].get(GRB.DoubleAttr.X); 
				}
				
				//controllo il valore delle variabili di base
		        
		        for(int i=0; i<modelIncrementVars.length; i++) {
		        	
		        	//guarda se cambia il val di una qualche var in base
		        	if(originalVarValues[i]!=modelIncrementVarsValue[i]){
		        		
		        		dMax= distanzaIncr;
		        		
		        		basiUguali = false;
		        		break;
		        	}
		        	
		        	
		        	//Quando la distanza arriva a 199 mi fermo
		        	if(distanzaIncr>200){
		        		
		        		dMax=-1;
		        		basiUguali = false;
		        		break;
		        	}
		        }
		    }
		
			// 2 soluzione ottima trovata
			// 3 non esiste soluzione ammissibile (infeasible)
			// 5 soluzione illimitata

			}catch (GRBException e)
		{
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        //DIMINUISCO LA DISTANZA
        
        try
		{
        	GRBEnv envDecrement = new GRBEnv();
			
			envDecrement.set(IntParam.Presolve, 0);
			envDecrement.set(IntParam.Method, 0);
			envDecrement.set(GRB.IntParam.OutputFlag, 0);
			
			boolean basiUguali = true;
			
			int distanzaDecr = distanza;
			
			while(basiUguali) {
				
				distanzaDecr--;

				GRBModel modelDecrement = new GRBModel(envDecrement);
				
				// Creazione delle variabili di decisione
				GRBVar[][] xij = aggiungiVariabili(modelDecrement, produzione, domanda);
				
				// Aggiunta vincoli produzione
				aggiungiVincoliProduzione(modelDecrement, xij, produzione);
				
				
				//Creazione delle variabili binarie
				int[][] distanze= modificaDistanza(distanzaDecr);
				int[][] y = creaAusiliarie(distanze, k);
				
				// Aggiunta della funzione obiettivo
				aggiungiFunzioneObiettivo(modelDecrement, xij, distanze, c);
				
				
				
				// Aggiunta vincolo domanda
				aggiungiVincoliDomanda(modelDecrement, xij, domanda, y);
				modelDecrement.optimize();
				
				// Ciclo sulle var originali come quando incrementavo
				GRBVar[] modelDecrementVars = modelDecrement.getVars(); 
				double[] modelDecrementVarsValue = new double[modelDecrementVars.length]; 
				
				for (int i = 0; i < modelDecrementVars.length; i++) {
					modelDecrementVarsValue[i] = modelDecrementVars[i].get(GRB.DoubleAttr.X);
				}
				
				//qua controllo il valore delle variabili di base
		        
		        for(int i=0; i<modelDecrementVars.length; i++) {
		        	
		        	if(originalVarValues[i]!=modelDecrementVarsValue[i]){
		        		
		        		dMin= distanzaDecr;
		        		basiUguali = false;
		        		break;
		        	}
		        	
		        	//quando porto la distanza dxy a 0 mi fermo
		        	if(distanzaDecr==0){

		        		basiUguali = false;
		        		break;
		        	}
		        }
		    }
		
			// 2 soluzione ottima trovata
			// 3 non esiste soluzione ammissibile (infeasible)
			// 5 soluzione illimitata

			}catch (GRBException e)
		{
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
        
        
        if(dMax==-1) {
        	 System.out.println("Intervallo d_xy = ["+dMin+ ", "+ "INF]" );
        }
        else {
        	System.out.println("Intervallo d_xy = ["+dMin+ ", "+ dMax+"]" );
        }
        
        
 }


	private static int[][] modificaDistanza(int distanzaIncr) {
		//mi copio la matrice d_ij
				int[][] distanze = new int[m][n];
				
				
				for(int i=0; i<m; i++) {
					for(int j=0; j<n; j++){
						
						if(i==x && j==y) {
							distanze[i][j] = distanzaIncr;
						}
						else {
							distanze[i][j] = d_ij[i][j];
						}
					}
				}
				return distanze;
	}



	private static void aggiungiModificaFunzioneOb(GRBModel modelIncrement, GRBVar[][] xij, int distanza) throws GRBException {
		GRBLinExpr obj = new GRBLinExpr();

		for (int i = 0; i < m; i++){
			for (int j = 0; j < n; j++){
				if(i==x && j==y) {
					obj.addTerm(distanza*c, xij[i][j]);
				}
					obj.addTerm(d_ij[i][j]*c, xij[i][j]);
			}
		}
		modelIncrement.setObjective(obj);
		modelIncrement.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
	}


	private static void faiAnalisiSensitivitaMagazzino(GRBModel model, int magazzinoH) throws GRBException {
		// Ciclo sulle var originali
		GRBVar[] originalVars = model.getVars(); // Ottiene tutte le variabili del modello
		double[] originalVarValues = new double[originalVars.length]; // Crea un array per i valori delle variabili
		for (int i = 0; i < originalVars.length; i++) {
		    originalVarValues[i] = originalVars[i].get(GRB.DoubleAttr.X); // Aggiunge il valore della variabile all'array
		}
        System.out.println("");
        
        double capacitaInc = produzione[magazzinoH];
        
        //incremento la capacità
        try
		{
			GRBEnv envIncrement = new GRBEnv();
			
			envIncrement.set(IntParam.Presolve, 0);
			envIncrement.set(IntParam.Method, 0);
			envIncrement.set(GRB.IntParam.OutputFlag, 0);
			
			boolean basiUguali = true;

			while(basiUguali) {
				
				
				
				GRBModel modelIncrement = new GRBModel(envIncrement);
				
				// Creazione delle variabili di decisione
				GRBVar[][] xij = aggiungiVariabili(modelIncrement, produzione, domanda);
				
				// Aggiunta vincoli produzione
				aggiungiModificaVincoliProduzione(modelIncrement, xij, capacitaInc++, magazzinoH);
				//aggiungiVincoliProduzione(modelIncrement, xij, produzione);

				// Aggiunta della funzione obiettivo
				aggiungiFunzioneObiettivo(modelIncrement, xij, d_ij, c);
				//Creazione delle variabili binarie
				int[][] y = creaAusiliarie(d_ij, k);
				// Aggiunta vincolo domanda
				aggiungiVincoliDomanda(modelIncrement, xij, domanda, y);
				modelIncrement.optimize();
				
				//ricavo valori delle variabili di modelIncrement
				
				// Ciclo sulle var originali
				GRBVar[] modelIncrementVars = modelIncrement.getVars(); // Ottiene tutte le variabili del modello
				double[] modelIncrementVarsValue = new double[modelIncrementVars.length]; // Crea un array per i valori delle variabili
				
				for (int i = 0; i < modelIncrementVars.length; i++) {
					modelIncrementVarsValue[i] = modelIncrementVars[i].get(GRB.DoubleAttr.X); // Aggiunge il valore della variabile all'array
				}
		        System.out.println("");
				
				
				//qua devo controllare il valore delle variabili di base
		        
		        for(int i=0; i<modelIncrementVars.length; i++) {
		        	
		        	if(originalVarValues[i]!=modelIncrementVarsValue[i]){
		        		
		        		basiUguali = false;
		        		break;
		        	}
		        }
			}
		
			// 2 soluzione ottima trovata
			// 3 non esiste soluzione ammissibile (infeasible)
			// 5 soluzione illimitata

			}catch (GRBException e)
		{
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
        
        double capacitaDec = produzione[magazzinoH];
        
        try
		{
			GRBEnv envDecrement = new GRBEnv();
			
			envDecrement.set(IntParam.Presolve, 0);
			envDecrement.set(IntParam.Method, 0);
			envDecrement.set(GRB.IntParam.OutputFlag, 0);
			
			boolean basiUguali = true;

			while(basiUguali) {
				
				
				
				GRBModel modelDecrement = new GRBModel(envDecrement);
				
				// Creazione delle variabili di decisione
				GRBVar[][] xij = aggiungiVariabili(modelDecrement, produzione, domanda);
				
				// Aggiunta vincoli produzione
				aggiungiModificaVincoliProduzione(modelDecrement, xij, capacitaDec--, magazzinoH);
				//aggiungiVincoliProduzione(modelIncrement, xij, produzione);

				// Aggiunta della funzione obiettivo
				aggiungiFunzioneObiettivo(modelDecrement, xij, d_ij, c);
				//Creazione delle variabili binarie
				int[][] y = creaAusiliarie(d_ij, k);
				// Aggiunta vincolo domanda
				aggiungiVincoliDomanda(modelDecrement, xij, domanda, y);
				modelDecrement.optimize();
				
				//ricavo valori delle variabili di modelIncrement
				
				// Ciclo sulle var originali
				GRBVar[] modelDecrementVars = modelDecrement.getVars(); // Ottiene tutte le variabili del modello
				double[] modelDecrementVarsValue = new double[modelDecrementVars.length]; // Crea un array per i valori delle variabili
				
				for (int i = 0; i < modelDecrementVars.length; i++) {
					modelDecrementVarsValue[i] = modelDecrementVars[i].get(GRB.DoubleAttr.X); // Aggiunge il valore della variabile all'array
				}
		        System.out.println("");
				
				
				//qua devo controllare il valore delle variabili di base
		        
		        for(int i=0; i<modelDecrementVars.length; i++) {
		        	
		        	if(originalVarValues[i]!=modelDecrementVarsValue[i]){
		        		
		        		basiUguali = false;
		        		break;
		        	}
		        }
			}
		
			// 2 soluzione ottima trovata
			// 3 non esiste soluzione ammissibile (infeasible)
			// 5 soluzione illimitata

			}catch (GRBException e)
		{
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
        
        
       System.out.println("Intervallo s_"+ h + ": ["+ capacitaDec + "," + capacitaInc+"]");
		
	}


	private static void aggiungiModificaVincoliProduzione(GRBModel model, GRBVar[][] xij,
			double capacita, int magazzinoH) throws GRBException {
		
		for (int i = 0; i < m; i++){
			GRBLinExpr expr = new GRBLinExpr();

			for (int j = 0; j < n; j++){
				expr.addTerm(1, xij[i][j]);
			}
			if(i==magazzinoH) {
				model.addConstr(expr, GRB.LESS_EQUAL, capacita, "vincolo_produzione_i_"+i);
			}
			else {
				model.addConstr(expr, GRB.LESS_EQUAL, produzione[i], "vincolo_produzione_i_"+i);
			}
		}
	}


	private static void stampaIntervalloK() {
	
		int minDistanza=0;
		int maxDistanza =0;
		int kMax=0;
		int kMin=0;
		
		 for (int i = 0; i < d_ij.length; i++) {
	         for (int j = 0; j < d_ij[i].length; j++) {
	            if (d_ij[i][j] > maxDistanza) {
	               maxDistanza = d_ij[i][j];
	            }
	            if (d_ij[i][j] < minDistanza) {
	               minDistanza = d_ij[i][j];
	            }
	         }
	      }

		try
		{
			GRBEnv envK = new GRBEnv();
			
			envK.set(IntParam.Presolve, 0);
			envK.set(IntParam.Method, 0);
			envK.set(GRB.IntParam.OutputFlag, 0);

			for(int i=0; i<=maxDistanza; i++) {
				
				GRBModel modelK = new GRBModel(envK);
				
				// Creazione delle variabili di decisione
				GRBVar[][] xij = aggiungiVariabili(modelK, produzione, domanda);
				
				// Aggiunta vincoli produzione
				aggiungiVincoliProduzione(modelK, xij, produzione);

				// Aggiunta della funzione obiettivo
				aggiungiFunzioneObiettivo(modelK, xij, d_ij, c);
				
				//Creazione delle variabili binarie
				int[][] y = creaAusiliarie(d_ij, i);
				
				// Aggiunta vincolo domanda
				aggiungiVincoliDomanda(modelK, xij, domanda, y);
				
				modelK.optimize();
				
				int status = modelK.get(GRB.IntAttr.Status);
				
				if(status == 3 || status ==5) {
					
					if(i<=kMin) {
						kMin=i;
					}
					if(i>=kMax) {
						kMax=i;
					}
				}
				else if(status == 2) {
					
					break;
				}
				
				System.out.println();
				
			}
			System.out.println("Intervallo k = (" + kMin + ", " + kMax + ")");
			// 2 soluzione ottima trovata
			// 3 non esiste soluzione ammissibile (infeasible)
			// 5 soluzione illimitata

			}catch (GRBException e)
		{
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
}


	private static void risolviDuale(GRBModel model) {

		            
		            GRBModel modelD;
					try {
						modelD = model.dualize();
						 modelD.optimize();
				            
				    		
				    		GRBVar[] dualVar = modelD.getVars();  
				    		
				    		System.out.println("\nSoluzione duale: ");	    		
				    		
				    		for(int i=0; i<dualVar.length; i++) {
				    			
				    			int index = i+1;
				    			System.out.print("Lambda "+ index + ": " );
				    			System.out.printf("%.04f, ", Math.abs(dualVar[i].get(GRB.DoubleAttr.X)));
				    			System.out.println("");
				    		}
				    		
					} catch (GRBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}    		
}


	private static GRBVar[][] aggiungiVariabili(GRBModel model, int[] produzione, int[] domanda) throws GRBException{
		GRBVar[][] xij = new GRBVar[produzione.length][domanda.length];

		for (int i = 0; i < m; i++) //matrice mxn 25x51
		{
			for (int j = 0; j < n; j++)
			{
				xij[i][j] = model.addVar(0, GRB.INFINITY, 0, GRB.CONTINUOUS, "xij_"+i+"_"+j);
			}
		}
		return xij;
}
	
	private static void aggiungiFunzioneObiettivo(GRBModel model, GRBVar[][] xij, int[][] distanze, double costoKm) throws GRBException{
		GRBLinExpr obj = new GRBLinExpr();

		for (int i = 0; i < m; i++){
			for (int j = 0; j < n; j++){
					obj.addTerm(distanze[i][j]*costoKm, xij[i][j]);
			}
		}
		model.setObjective(obj);
		model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
	}
	
	private static void aggiungiVincoliProduzione(GRBModel model, GRBVar[][] xij, int[] produzione) throws GRBException{
		
		for (int i = 0; i < m; i++){
			GRBLinExpr expr = new GRBLinExpr();

			for (int j = 0; j < n; j++){
				expr.addTerm(1, xij[i][j]);
			}
			model.addConstr(expr, GRB.LESS_EQUAL, produzione[i], "vincolo_produzione_i_"+i);
		}
	}
	
	
	public static GRBConstr[] ottieniVincoliProduzione(GRBModel model, GRBVar[][] xij) throws GRBException {
		GRBConstr[] vincoliProd = new GRBConstr[produzione.length];
		for (int i = 0; i < m; i++){
			GRBLinExpr expr = new GRBLinExpr();

			for (int j = 0; j < n; j++){
				expr.addTerm(1, xij[i][j]);
			}
			
			vincoliProd[i] = model.addConstr(expr, GRB.LESS_EQUAL, produzione[i], "vincolo_produzione_i_"+i);
		}
		
		return vincoliProd;
	}
	
	private static void aggiungiVincoliDomanda(GRBModel model, GRBVar[][] xij, int[] domanda, int[][] y) throws GRBException{
		
		for (int j = 0; j < n; j++){
			GRBLinExpr expr = new GRBLinExpr();

			for (int i = 0; i < m; i++){
				if(y[i][j]==1) {
					expr.addTerm(1, xij[i][j]);
				}
				
			}
			model.addConstr(expr, GRB.GREATER_EQUAL, domanda[j], "vincolo_domanda_j_"+j);
		}
	}

	
	
	private static void risolvi(GRBModel model) throws GRBException
	{
		model.optimize();
		// 2 soluzione ottima trovata
		// 3 non esiste soluzione ammissibile (infeasible)
		// 5 soluzione illimitata
		// 9 tempo limite raggiunto

		for(GRBVar var : model.getVars()){
			
			System.out.println(var.get(StringAttr.VarName)+ ": "+ var.get(DoubleAttr.X));
		}
	}

	private static void stampaVincoliInattivi(GRBModel model) throws GRBException {
	    // Risolve il modello
	    model.optimize();

	    // Ottiene il valore ottimo della funzione obiettivo
	    double objValue = model.get(GRB.DoubleAttr.ObjVal);

	    // Cicla sui vincoli e verifica se sono attivi o meno
	    GRBConstr[] constraints = model.getConstrs();
	    
	    System.out.print("Lista vincoli non attivi: [");
	    
	    for (int i = 0; i < constraints.length; i++) {
	        GRBConstr constraint = constraints[i];
	        double slack = constraint.get(GRB.DoubleAttr.Slack);
	        if (slack != 0.0) {
	            // Il vincolo non è attivo
	            System.out.print(constraint.get(GRB.StringAttr.ConstrName)+",");
	        }
	    }
	    System.out.println("");
	}
}


