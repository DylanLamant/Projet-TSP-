/*
 * 
 */
package tsp;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * This class is the place where you should enter your code and from which you can create your own objects.
 * 
 * The method you must implement is solve(). This method is called by the programmer after loading the data.
 * 
 * The TSPSolver object is created by the Main class.
 * The other objects that are created in Main can be accessed through the following TSPSolver attributes: 
 * 	- #m_instance :  the Instance object which contains the problem data
 * 	- #m_solution : the Solution object to modify. This object will store the result of the program.
 * 	- #m_timeLimit : the maximum time limit (in seconds) given to the program.
 *  
 * @author Damien Prot, Fabien Lehuede, Axel Grimault
 * @version 2017
 * 
 */
public class TSPSolver {

	// -----------------------------
	// ----- ATTRIBUTS -------------
	// -----------------------------

	/**
	 * The Solution that will be returned by the program.
	 */
	private Solution m_solution;

	/** The Instance of the problem. */
	private Instance m_instance;

	/** Time given to solve the problem. */
	private long m_timeLimit;
	

	
	// -----------------------------
	// ----- CONSTRUCTOR -----------
	// -----------------------------

	/**
	 * Creates an object of the class Solution for the problem data loaded in Instance
	 * @param instance the instance of the problem
	 * @param timeLimit the time limit in seconds
	 */
	public TSPSolver(Instance instance, long timeLimit) {
		m_instance = instance;
		m_solution = new Solution(m_instance);
		m_timeLimit = timeLimit;
	}

	// -----------------------------
	// ----- METHODS ---------------
	// -----------------------------
	
	
	
	
	
	/**
	 * Méthode permettant de renverser la parcours allant de l'indice i à j dans une ArrayList 
	 * Exemple : sur le parcours [2,3,1,4],  RenverseParcours (1,3,parcours) retourne [2,4,1,3].
	 *
	 * @param i indice de début
	 * @param j indice de fin
	 * @param parcours le parcours sur lequel on souhaite faire un renversement
	 * @return ArrayList obtenue après renversement du parcours
	 */
	public ArrayList<Integer> RenverseParcours (int i, int j, ArrayList<Integer> parcours) { // Renverse le parcours allant de i à j dans la liste parcours en supposant i>j 
		ArrayList<Integer> renverse = new ArrayList<Integer>();
		ArrayList<Integer> tampon = new ArrayList<Integer>();
		
		for(int k = 0; k < j-i+1; k++) {
			renverse.add(parcours.get(j-k));
		}
		
		for(int k = 0; k < i; k++) {
			tampon.add(parcours.get(k));
		}
		
		tampon.addAll(renverse);
		
		for(int k = j+1; k < parcours.size(); k++) {
			tampon.add(parcours.get(k));
		}
		
		parcours = tampon;
		return parcours;
	}
	
	/**
	 * Méthode retourant le gain réalisé sur la distance entre la ville à l'indice i et la ville à 
	 * l'indice j, c'est-à-dire la différence de coût entre la solution initiale et la solution où 
	 * une partie du parcours est renversée (sans croisement).
	 *
	 * @param ameliorationTrajet Nouveau chemin sur lequel on souhaite calculer le gain
	 * @param i indice de début
	 * @param j indice de fin
	 * @return double gain, le gain (algébrique) obtenu en comparant le trajet initial et le trajet 
	 * où une partie est renversée.
	 * @throws Exception the exception
	 */
	public double gain(ArrayList<Integer> ameliorationTrajet,int i,int j) throws Exception {
		int N = ameliorationTrajet.size();
		double gain = 0;
		if (i < j) {
			gain = this.m_instance.getDistances(ameliorationTrajet.get(i),ameliorationTrajet.get((j+N+1)%N))
					+this.m_instance.getDistances(ameliorationTrajet.get((i+N-1)%N),ameliorationTrajet.get(j))
					-this.m_instance.getDistances(ameliorationTrajet.get((i+N-1)%N),ameliorationTrajet.get(i))
					-this.m_instance.getDistances(ameliorationTrajet.get(j),ameliorationTrajet.get((j+N+1)%N));
		} else {
			gain = this.m_instance.getDistances(ameliorationTrajet.get((i+N-1)%N),ameliorationTrajet.get(j))
					-this.m_instance.getDistances((i+N-1)%N,ameliorationTrajet.get(i));
		}
		return gain;
	}
	
	/**
	 * Retourne un trajet obtenu à partir de l'algorithme du plus proche voisin 
	 *
	 * @return ArrayList L'ArrayList solution du TSP obtenue avec l'algorithme du plus proche voisin 
	 * @throws Exception 
	 */
	public ArrayList<Integer> initialisation() throws Exception {
		int m = this.getInstance().getNbCities();
		int a = 0;
		long distanceMin = Long.MAX_VALUE;
		int ville = 0;
		ArrayList<Integer> listeVilles = new ArrayList<Integer>();
		listeVilles.add(0, 0);
		for (int k = 1; k < m; k++) {
			a = listeVilles.get(k-1);
			distanceMin = Long.MAX_VALUE;
			ville = 0;
			for (int i = 0; i < m; i++) {
				if ((!listeVilles.contains(i)) && this.getInstance().getDistances(a, i) < distanceMin) {
					distanceMin = this.getInstance().getDistances(a, i);
					ville = i;
				}
			}
			listeVilles.add(ville);
		}
		listeVilles.add(0);
		return listeVilles;
	} 
	
	
	
	
	

	/**
	 * *TODO* Modify this method to solve the problem.
	 * 
	 * Do not print text on the standard output (eg. using System.out.print() or System.out.println()).
	 * This output is dedicated to the result analyzer that will be used to evaluate your code on multiple instances.
	 * 
	 * You can print using the error output (System.err.print() or System.err.println()).
	 * 
	 * When your algorithm terminates, make sure the attribute #m_solution in this class points to the solution you want to return.
	 * 
	 * You have to make sure that your algorithm does not take more time than the time limit #m_timeLimit.
	 * 
	 * @throws Exception may return some error, in particular if some vertices index are wrong.
	 */
	public void solve() throws Exception {	
		m_solution.print(System.err);
		long startTime = System.currentTimeMillis();
		long spentTime = 0;
		int N = this.m_instance.getNbCities();
		ArrayList<Integer> trajetInitial = this.initialisation();
		ArrayList<Integer> ameliorationTrajet = new ArrayList<Integer>();
		ArrayList<Integer> meilleurTrajet = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> permutationsEffectuees = new ArrayList<ArrayList<Integer>>();

		for (int k=0;k<N+1;k++) {
			ameliorationTrajet.add(trajetInitial.get(k));
			meilleurTrajet.add(trajetInitial.get(k));
		}
		
		double meilleureDistance = Double.MAX_VALUE;
		double distanceAvant = Double.MAX_VALUE;
		double distanceApres = Double.MAX_VALUE;
		int nombreTirageAuSort = 0;
		int indexVilleAlea1 = 1 ;
		int indexVilleAlea2 = 1 ;
		int villeAlea1 = 1;
		
		do
		{
			
		distanceAvant = distanceApres;

		for (int i=1;i<N+1;i++) {  
			for (int j=i+1;j<N+1;j++) {
				if (this.gain(ameliorationTrajet,i,j)<0.0) {   
					/* Si le gain est négatif, renverser l'ordre entre la ville i et j permet de
					 * deminuer la distance totale.
					 */
					ameliorationTrajet=this.RenverseParcours(i,j,ameliorationTrajet); 
				}
			}
		}
		
		distanceApres = 0.0;
		for (int i = 0; i < ameliorationTrajet.size()-1; i++) { // On calcule la distance après 
																//les appels à RenverseParcours.
			distanceApres += this.m_instance.getDistances(ameliorationTrajet.get(i), ameliorationTrajet.get(i+1));
		}
		
		if (distanceApres < meilleureDistance) { // Si la distance calculée est inférieure à la meilleure
			meilleurTrajet.clear();				 // distance, on la stocke et on met à jour la solution.
			permutationsEffectuees.clear();
			meilleureDistance = distanceApres;
			for (int i = 0; i < ameliorationTrajet.size(); i++) {
				this.getSolution().setCityPosition(ameliorationTrajet.get(i),i);
				meilleurTrajet.add(ameliorationTrajet.get(i));
			}
		}
		/* Si la distance totale avant les appels à RenverseParcours est inférieure ou égale
		 * à la distance totale après, c'est qu'il n'y a pas eu d'amélioration et il n'y en aura plus
		 * pour cette liste là car un minimum local a été atteint.
		 * 
		 * Le but est alors d'inverser aléatoirement des couples de villes dans la liste représentant
		 * notre meilleure solution, et de réappliquer RenverseParcours. Tant qu'on ne trouve pas de 
		 * meilleure solution après inversion de 2 villes, on choisit un autre couple à inverser. 
		 * 
		 * De plus, si l'inversion ne permet pas d'amélioration, on repart de la meilleure solution
		 * pour inverser un autre couple, et non pas de la liste telle qu'elle est après la précédente
		 * inversion.
		 */
		if (distanceAvant <= distanceApres) {
			ameliorationTrajet.clear();
			for (int i = 0; i < meilleurTrajet.size(); i++) {
				ameliorationTrajet.add(meilleurTrajet.get(i));
			}
			ArrayList<Integer> couple1 = new ArrayList<Integer>();
			ArrayList<Integer> couple2 = new ArrayList<Integer>();
			indexVilleAlea1 = 1 + (int) (Math.random()*(N-1));
			indexVilleAlea2 = 1 + (int) (Math.random()*(N-1));
			couple1.add(indexVilleAlea1);
			couple1.add(indexVilleAlea2);
			couple2.add(indexVilleAlea2);
			couple2.add(indexVilleAlea1);
			nombreTirageAuSort = 0;
			
			/* couple1 et couple2 représente la même inversion. Si cette inversion n'a jamais été
			 * effectuée, alors on l'effectue et on la stock dans une liste. Sinon, on cherche
			 * un nouveau couple par au plus 1000 tirages au sort.
			 * 
			 * Le test contains n'est effectué que sur le couple1, car si la liste contient couple2, 
			 * alors elle contient aussi couple1 et inversement.
			 */
			
			while ((indexVilleAlea1 == indexVilleAlea2 || permutationsEffectuees.contains(couple1)) 
					 && nombreTirageAuSort < 1000) {
				couple1.clear();
				couple2.clear();
				indexVilleAlea1 = 1 + (int) (Math.random()*(N-1));
				indexVilleAlea2 = 1 + (int) (Math.random()*(N-1));
				couple1.add(indexVilleAlea1);
				couple1.add(indexVilleAlea2);
				couple2.add(indexVilleAlea2);
				couple2.add(indexVilleAlea1);
				nombreTirageAuSort ++;
			}
			
			// Cas où l'on a trouvé une inversion faisable par tirage au sort d'un couple:
			
			if (nombreTirageAuSort < 1000 ) {
				permutationsEffectuees.add(couple1);
				permutationsEffectuees.add(couple2);
				villeAlea1 = ameliorationTrajet.get(indexVilleAlea1); 
				ameliorationTrajet.set(indexVilleAlea1,ameliorationTrajet.get(indexVilleAlea2));
				ameliorationTrajet.set(indexVilleAlea2,villeAlea1);
				
			/*Si jamais toutes les inversions possibles ont été effectuées (petites instances) ou
			* si le tirage aléatoire ne parvient pas à trouver un nouveau couple en moins de 1000
			* tirages (une grande majorité de couples ayant déjà été trouvés), on décide de recommencer
			* en partant d'une nouvelle solution aléatoire (on conserve quand même la
			* solution trouvée avant la réinitialisation tant que l'on n'a pas trouvé mieux).
			* 
			* Le nombre de couples possibles à inverser étant de l'ordre de factoriel n, la limite de
			* 1000 tirages au sort sans trouver de nouveau couples n'est atteinte que pour les petites 
			* instances, où un minimum local sera plus vite atteint (le nombre de tirage au sort est
			* remis à 0 après avoir trouvé un nouveau couple).
			* 
			* Le nombre de 1000 a été choisi après plusieurs tests, de manière à laisser le temps aux
			* petites instances (moins de 150 villes) de trouver de nouveaux couples, sans que cette
			* tâche n'handicape trop la complexité temporelle.
			*/
				
			} else {
				
				distanceApres = Double.MAX_VALUE;
				permutationsEffectuees.clear();
				trajetInitial = this.initialisation();
				nombreTirageAuSort = 0;
				while (nombreTirageAuSort<100000) {
					indexVilleAlea1 = 1 + (int) (Math.random()*(N-1));
					indexVilleAlea2 = 1 + (int) (Math.random()*(N-1));
					villeAlea1 = trajetInitial.get(indexVilleAlea1); 
					trajetInitial.set(indexVilleAlea1,trajetInitial.get(indexVilleAlea2));
					trajetInitial.set(indexVilleAlea2,villeAlea1);
					nombreTirageAuSort ++;
				}
				ameliorationTrajet.clear();
				meilleurTrajet.clear();
				for (int k=0;k<N+1;k++) {
					ameliorationTrajet.add(trajetInitial.get(k));
					meilleurTrajet.add(trajetInitial.get(k));
				}
			}
		}
		spentTime=System.currentTimeMillis()-startTime;	
		} while(spentTime < (m_timeLimit * 1000 - 500) );
		
	/* Code du solve() pour l'algorithme des fourmis :
	    m_solution.print(System.err);
		long startTime = System.currentTimeMillis();
		long spentTime = 0;
		
		int N=this.m_instance.getNbCities(); // nombre de villes
		int m=N; //nombre de fourmis
		ArrayList<ArrayList<Integer>> visiteIterAvant = new ArrayList<ArrayList<Integer>>(); 
		for (int f = 0; f < m; f++) {
			visiteIterAvant.add(this.initialisation());
		}
		ArrayList<Integer> meilleureSolution = this.initialisation();
		double meilleureDistance = 0.0;
		for (int i = 0; i<this.initialisation().size()-1; i++) {
			this.getSolution().setCityPosition(meilleureSolution.get(i), i);
			meilleureDistance += this.m_instance.getDistances(meilleureSolution.get(i), meilleureSolution.get(i+1));
		}
		this.getSolution().setCityPosition(meilleureSolution.get(0), meilleureSolution.size()-1);
		ArrayList<ArrayList<Integer>> visiteIterNow = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < m; i++) {
			visiteIterNow.add(new ArrayList<Integer>());
		}
		double[][] visiFinale = this.visibilite();
		int nombreIteration = 0;
		ArrayList<ArrayList<Integer>> avisiter=new ArrayList<ArrayList<Integer>>();
		int depart = 0;
		double[] proba = new double[N];
		double maxi = 0.0;
		int w = 0;
		int indexTrajetMin = 0;
		double longueurTrajet = 0;
		do
		{
		spentTime = System.currentTimeMillis() - startTime;
		
		for (int i=0;i<N;i++) {
			avisiter.add(new ArrayList<Integer>());
			
			for (int j=0; j<N; j++) {
				avisiter.get(i).add(j);
			}
		}
		
		while (avisiter.get(N-1).size() != 0) {
			for (int k=0;k<m;k++) { 
				visiteIterNow.get(k).add(k);
				depart = k;
				for (int h=0; h<N; h++) {
					proba=this.probaDeVisite(k, depart, visiteIterAvant, avisiter, visiFinale);
					maxi = 0.0;
					w = 0;
					for (int n=0; n<proba.length; n++) {
						if (proba[n]>maxi) {
							w=n;
							maxi=proba[n];
						}
					}
					
					depart = w;
					visiteIterNow.get(k).add(depart);
					avisiter.get(k).remove((Object) depart);
				}
			}
		}
		
		for (int i = 0; i < visiteIterNow.size(); i++) {
			visiteIterAvant.get(i).clear();
			
			for (int j = 0; j < visiteIterNow.get(i).size(); j++) {
				visiteIterAvant.get(i).add(visiteIterNow.get(i).get(j));
			}
		}
		
		for (int k=0; k<visiteIterNow.size(); k++) {
			visiteIterNow.get(k).clear();
		}
		
		indexTrajetMin = 0;
		
		for (ArrayList<Integer> parcours : visiteIterAvant) {
			if (parcours.get(0)==parcours.get(parcours.size()-1)) {
				longueurTrajet = 0;
				for (int i = 0; i < N; i++) {
					longueurTrajet += this.m_instance.getDistances(parcours.get(i), parcours.get(i+1));
				}
			
				if (longueurTrajet<meilleureDistance) {
					meilleureDistance=longueurTrajet;
					indexTrajetMin = visiteIterAvant.indexOf(parcours);
					
					for (int i=0; i<N+1; i++) {
						this.getSolution().setCityPosition(visiteIterAvant.get(indexTrajetMin).get(i), i);
					}	
				}
			}
		}
		nombreIteration++;		
	}	while(spentTime < (m_timeLimit * 1000 - 100) );
		System.out.println(nombreIteration);
	 */
	}

	

	// -----------------------------
	// ----- GETTERS / SETTERS -----
	// -----------------------------

	/** @return the problem Solution */
	public Solution getSolution() {
		return m_solution;
	}

	/** @return problem data */
	public Instance getInstance() {
		return m_instance;
	}

	/** @return Time given to solve the problem */
	public long getTimeLimit() {
		return m_timeLimit;
	}

	/**
	 * Initializes the problem solution with a new Solution object (the old one will be deleted).
	 * @param solution : new solution
	 */
	public void setSolution(Solution solution) {
		this.m_solution = solution;
	}

	/**
	 * Sets the problem data
	 * @param instance the Instance object which contains the data.
	 */
	public void setInstance(Instance instance) {
		this.m_instance = instance;
	}

	/**
	 * Sets the time limit (in seconds).
	 * @param time time given to solve the problem
	 */
	public void setTimeLimit(long time) {
		this.m_timeLimit = time;
	}
	
	// Les méthodes qui suivent sont utilisées pour l'algorithme fourmi
	
	/**
	 * Méthode retournant une matrice des inverses des distances entre les villes i et j
	 *
	 * @return visi matrices de double des inverses des distances.
	 * @throws Exception the exception
	 */
	public double[][] visibilite() throws Exception{
		double[][] visi=new double[this.m_instance.getNbCities()][this.m_instance.getNbCities()];
		for (int i=0;i<this.m_instance.getNbCities();i++) {
			for (int j=0;j<this.m_instance.getNbCities();j++) {
				if (i!=j) {
					visi[i][j]=1.0/this.m_instance.getDistances(i,j);
				} else {
					visi[i][j]=0;
				}
			}
		}
		return visi;
	}
	
	/**
	 * @param visiteIterNow liste des listes de villes déjà visitées par les fourmis dans l'ordre de visite.
	 * @return pheromone matrice de doubles de la quantité totale de phéromones sur le trajet ij
	 * @throws Exception
	 */
	public double[][] pheromone(ArrayList<ArrayList<Integer>> visiteIterNow) throws Exception {
		int N = this.m_instance.getNbCities();
		double[][] pheromone = new double[N][N];
		Double[][] pt = this.pherotrajet(visiteIterNow); 
		double rho = 0;
		for (int i = 0; i < pt.length; i++) {
			for (int j=0; j<pheromone.length; j++) {
				pheromone[i][j] = 0.001;
				pheromone[visiteIterNow.get(i).get(j)][visiteIterNow.get(i).get(j+1)] = pt[i][j] + ((1-rho)*pheromone[visiteIterNow.get(i).get(j)][visiteIterNow.get(i).get(j+1)]);
			}
		}
		return pheromone;
	}
	
	
	/**
	 * @param visiteIterNow liste des listes de villes déjà visitées par les fourmis dans l'ordre de visite.
	 * @return ptrajet2 matrice de doubles de la quantité de phéromones déposée pendant l'itération
	 * actuelle sur le trajet ij
	 * @throws Exception
	 */
	public Double[][] pherotrajet(ArrayList<ArrayList<Integer>> visiteIterNow) throws Exception {
		ArrayList<Double> ptrajet1 = new ArrayList<Double>();
		Double[][] ptrajet2 = new Double[visiteIterNow.size()][visiteIterNow.size()];
		int N = this.m_instance.getNbCities();
		double constanteQ = 1;
		for (ArrayList<Integer> parcours : visiteIterNow) {
			double longueurTrajet = 0.0;
			for (int i = 0; i < N-1; i++) {
				longueurTrajet += this.m_instance.getDistances(parcours.get(i), parcours.get(i+1));
			}
			ptrajet1.add(constanteQ/longueurTrajet);
		}
		for (int i=0; i<ptrajet2.length; i++) {
			for (int j=0; j<ptrajet2.length; j++) {
				ptrajet2[i][j] = 0.0;
			}
		}
		
		for (int k = 0; k < visiteIterNow.size(); k++) {
			for (int i = 0; i < visiteIterNow.get(0).size()-1; i++) {
				ptrajet2[visiteIterNow.get(k).get(i)][visiteIterNow.get(k).get(i+1)] += ptrajet1.get(k);
			}
		}
		return ptrajet2;
	}
	
	
	/**
	 * @param k numéro de la liste de villes, représentant le trajet de la fourmi k
	 * @param depart numéro de la ville sur laquelle la fourmi est, c'est à dire la dernière ville visitée
	 * @param visiteavant liste des listes de villes déjà visitées
	 * @param avisiter liste des listes de villes qu'il reste à visiter pour chaque fourmi
	 * @param visiFinale matrice des visibilités
	 * @return proba, tableau de probabilités de visite de chaque ville restante par la fourmi k
	 * @throws Exception
	 */
	public double[] probaDeVisite(int k,int depart , ArrayList<ArrayList<Integer>> visiteavant, ArrayList<ArrayList<Integer>> avisiter, double[][] visiFinale) throws Exception {
		int N = visiteavant.size();
		double[][] pherofinal = this.pheromone(visiteavant);
		double[] proba = new double[N];
		double alpha = 0.5;
		double beta = 1;
		double gamma = 0.0;
		double s =0.0;
		for (int i=0;i<N;i++) {
			proba[i]=gamma;
			if (avisiter.get(k).contains(i)) {
				s += gamma + Math.pow(pherofinal[depart][i],alpha)*Math.pow(visiFinale[depart][i],beta);			
			}
		}
		for (int j=0; j<avisiter.get(k).size();j++) {
			double p = (gamma +(Math.pow(pherofinal[depart][avisiter.get(k).get(j)],alpha)*Math.pow(visiFinale[depart][avisiter.get(k).get(j)],beta)))/s;
			proba[avisiter.get(k).get(j)] = p;
		}
		return proba;
	}
	
}