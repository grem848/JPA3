/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package querytester;

import entity.City;
import entity.Country;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author Andreas Heick Laptop
 */
public class QueryRunner
{

    public static void main(String[] args)
    {
        EntityManager em = Persistence.createEntityManagerFactory("pu").createEntityManager();

        try
        {
            // NamedQuery Untyped
            Query query = em.createNamedQuery("City.findAll");
            List<City> cities = query.getResultList();
            System.out.println("Number of cities: " + cities.size());

            // NamedQuery Typed
            TypedQuery<City> qt = em.createNamedQuery("City.findAll", City.class);
            String cityName = qt.getResultList().get(0).getName();
            System.out.println("Name of city: " + cityName);

            // Dynamic Query
            Query q1 = em.createQuery("SELECT COUNT(c) FROM City c");
//            System.out.println("Number of cities: " + q1.getSingleResult());
            long result = (long) q1.getSingleResult();
            System.out.println("Number of cities: " + result);

            // Dynamic Query
            Query q2 = em.createQuery("select max(c.population) from Country c");
            int population = (int) q2.getSingleResult();
            System.out.println("Population: " + population);
            
            // NamedQuery Typed
            TypedQuery<Country> q3 = em.createNamedQuery("Country.findByPopulation", Country.class);
            q3.setParameter("population", population);
            System.out.println("Country with max population: " + q3.getSingleResult().getName());
            
            // Dynamic Query
            Query q4 = em.createQuery("select c from Country c where c.population=(SELECT MAX(c.population) from Country c)");
            Country c2 = (Country) q4.getSingleResult();
            System.out.println("Country with max population: " + c2.getName() + " - Population: " + c2.getPopulation());
            
            // Dynamic Query
            Query q5 = em.createQuery("SELECT c from Country c where c.population > :population ORDER BY c.name").setParameter("population", 100_000_000);
            List<Country> countries = q5.getResultList();
            Stream<String> names = countries.stream().map(country -> country.getName());
            String[] temp = names.toArray(String[]::new);
            String namesStr = String.join(", ", temp);
            System.out.println("Countries sorted by name, with population over 100 mil: " + namesStr);
            
        } finally
        {
            em.close();
        }
    }

}
