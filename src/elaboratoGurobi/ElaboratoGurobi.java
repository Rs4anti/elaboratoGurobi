package elaboratoGurobi;
/*L’azienda Lambdamatic è attiva sul territorio con m magazzini ed ha stipulato un contratto di fornitura esclusiva
di caffé in grani con n clienti. Ogni giorno, ogni cliente i deve essere rifornito di ri Kg di caffé. Per soddisfare le
domande, l’azienda dovrà procedere all’invio della merce stoccata nei suoi m diversi magazzini. La capacità massima
di stoccaggio di ciascun magazzino j è fissa ed è pari a sj Kg. La distanza da ciascun magazzino j a ciascun punto
vendita i è nota ed è pari a dji Km. Per trasportare 1 Kg di merce per 1 Km, l’azienda stima un costo pari a c euro.
Lambdamatic vuole soddisfare tutte le domande minimizzando il costo complessivo di trasporto, assicurandosi altresı̀
che ciascun punto vendita i sia servito solo da magazzini posizionati nel raggio di k Km.*/

import gurobi.*;
import gurobi.GRB.IntParam;

public class ElaboratoGurobi
{
	int m = 25; //numero magazzini
	int n = 51; //numero clienti
	int[][] d_ij= new int[m][n];
	int[] r_i = new int[n];
	int[] s_j = new int[m];
	double c = 0.01;
	int k = 12; //raggio Km
	int[] alfa_j = new int[m]; //	quesito III
	int h = 14; //Questo II capacità magazzino 14
	int x = 3;
	int y = 4;
	
	public static void main(String[] args)
	{
		try
		{
			GRBEnv env = new GRBEnv("elaboratoGurobi.log");
			
			env.set(IntParam.Presolve, 0);
			env.set(IntParam.Method, 0);
			
			GRBModel model = new GRBModel(env);
			
			// Creazione delle variabili
			GRBVar x = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "x");
			GRBVar y = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "y");
			GRBVar z = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "z");
			
			// Aggiunta della funzione obiettivo: max x + y + 2 z
			GRBLinExpr expr = new GRBLinExpr();
			expr.addTerm(1.0, x);
			expr.addTerm(1.0, y);
			expr.addTerm(2.0, z);
			model.setObjective(expr, GRB.MAXIMIZE);
			
			// Aggiunta del vincolo: x + 2 y + 3 z <= 4
			expr = new GRBLinExpr();
			expr.addTerm(1.0, x);
			expr.addTerm(2.0, y);
			expr.addTerm(3.0, z);
			model.addConstr(expr, GRB.LESS_EQUAL, 4.0, "c0");
			
			// Aggiunta del vincolo: x + y >= 1
			expr = new GRBLinExpr();
			expr.addTerm(1.0, x);
			expr.addTerm(1.0, y);
			model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "c1");
			
			// Ottimizza il modello
			model.optimize();
			
			System.out.println(x.get(GRB.StringAttr.VarName) + " " + x.get(GRB.DoubleAttr.X));
			System.out.println(y.get(GRB.StringAttr.VarName) + " " + y.get(GRB.DoubleAttr.X));
			System.out.println(z.get(GRB.StringAttr.VarName) + " " + z.get(GRB.DoubleAttr.X));
			System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
			
			
			
			System.out.println(model.getVarByName("x").get(GRB.StringAttr.VarName) + " "
					+ model.getVarByName("x").get(GRB.DoubleAttr.X) + " RC: " + model.getVarByName("x").get(GRB.DoubleAttr.RC));
			System.out.println(model.getVarByName("y").get(GRB.StringAttr.VarName) + " "
					+ model.getVarByName("y").get(GRB.DoubleAttr.X) + " RC: " + model.getVarByName("y").get(GRB.DoubleAttr.RC));
			System.out.println(model.getVarByName("z").get(GRB.StringAttr.VarName) + " " 
					+ model.getVarByName("z").get(GRB.DoubleAttr.X) + " RC: " + model.getVarByName("z").get(GRB.DoubleAttr.RC));
			System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
			
			// Libera le risorse associate a modello ed env
			model.dispose();
			env.dispose();
		} catch (GRBException e)
		{
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}
}
