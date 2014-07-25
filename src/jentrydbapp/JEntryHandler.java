/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jentrydbapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jpa.entities.JERefs;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author ransomkb
 */
public class JEntryHandler extends DefaultHandler
{
    private boolean bKeb = false; // Kanji element exists
    private boolean bKinf = false; // Kanji orthographical element exists
    private boolean bKpri = false; // Kanji priority element exists
    private boolean bReading = false; // Reading element exists
    private boolean bRnoKan = false; // Reading element not connected to K directly exists
    private boolean bRpri = false; // Reading priority element exists
    private boolean bMeaning = false; // Eng meaning exists
    private boolean bExample = false; // Eng meaning exists
    private boolean bPos = false; // Part of Sentence exists
    //private boolean bKanji = false; // Kanji element exists
    private boolean bAnton = false; // Antonym element exists
    private boolean bField = false; // Field element exists
    private boolean bDial = false; // Dialect element exists
    
    private int count;
    private int countK;
    private int countR;
    private int countM;
    private int countS;
    
    private Serializer ser;
    
    private String kanji = "";
    private String reading = "";
    //private String meaning = "";
    //private String example = "";
    
    private JEntry ent = null;
    private List<JEntry> entList = null;

    // To persist a new entity
    // persistence unit name KanjiDBPU from persistence.xml
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("JEntryDBAppPU");
    EntityManager em = emf.createEntityManager();

    JEntryHandler() throws UnsupportedEncodingException
    {
        count = 0;
        countK = 0;
        countR = 0;
        countM = 0;
        countS = 0;
        
        entList = new ArrayList<>();
        ser = new Serializer();
    }

    public List<JEntry> getEntList()
    {
        return entList;
    }

    /**
     *shut down the EntityManager and factory
     */
    public void closePersistStuff()
    {
        em.close();
        emf.close();
    }
    
    @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
           throws SAXException
   {
       String elementName = qName;
       
       if(elementName == null)
           return;
       
       switch(elementName.toLowerCase()) 
       {
           case "entry":
               ent = new JEntry();
               break;
           case "k_ele":
               ent.setbKanji(true);
               break;
           case "keb":
               bKeb = true;
               if(ent.kElem.getKebs() == null)
               {
                   ent.kElem.setKebs(new ArrayList<>());
               }
               break;
           case "ke_inf":
               bKinf = true;
               if(ent.kElem.getKeInfs() == null)
               {
                   ent.kElem.setKeInfs(new ArrayList<>());
               }
               break;
           case "ke_pri":
               bKpri = true;
               if(ent.kElem.getKePris() == null)
               {
                   ent.kElem.setKePris(new ArrayList<>());
               }
               break;
           case "r_ele":
               ent.setbRead(true);
               break;
           case "reb":
               bReading = true;
               if(ent.rElem.getRebs() == null)
               {
                   ent.rElem.setRebs(new ArrayList<>());
               }
               break;
           case "re_nokanji":
               bRnoKan = true;
               if(ent.rElem.getReNokanjis() == null)
               {
                   ent.rElem.setReNokanjis(new ArrayList<>());
               }
               break;
           case "re_pri":
               bRpri = true;
               if(ent.rElem.getRePris() == null)
               {
                   ent.rElem.setRePris(new ArrayList<>());
               }
               break;
           case "sense":
               ent.setbMean(true);
               ent.sCont.setsElem(new SenseElem());
               ++countS;
               break;
           case "pos":
               bPos = true;
               ent.sCont.getsElem().setbPos(bPos);
               if(ent.sCont.getsElem().getPosList() == null)
               {
                   ent.sCont.getsElem().setPosList(new ArrayList<>());
               }
               break;
           case "ant":
               bAnton = true;
               if(ent.sCont.getsElem().getAntonymList() == null)
               {
                   ent.sCont.getsElem().setAntonymList(new ArrayList<>());
               }
               break;
           case "field":
               bField = true;
               if(ent.sCont.getsElem().getFieldList() == null)
               {
                   ent.sCont.getsElem().setFieldList(new ArrayList<>());
               }
               break;
           case "dial":
               bDial = true;
               if(ent.sCont.getsElem().getDialectList() == null)
               {
                   ent.sCont.getsElem().setDialectList(new ArrayList<>());
               }
               break;
           case "gloss":
               String lang = attributes.getValue("xml:lang");
           
               if(lang == null || lang.equalsIgnoreCase("eng"))
               {
                   bMeaning = true;
                   if(ent.sCont.getsElem().getMeaningList() == null)
                   {
                       ent.sCont.getsElem().setMeaningList(new ArrayList<>());
                   }
               }
               break;
           case "example":
               bExample = true;
               if(ent.sCont.getsElem().getExampleList()== null)
               {
                   ent.sCont.getsElem().setExampleList(new ArrayList<>());
               }
               break;
           default:
               break;
       }
   }   
   
   @Override
   public void endElement(String uri, String localName, String qName)
   {
       if(qName.equalsIgnoreCase("sense"))
       {
           SenseElem sElem = ent.sCont.getsElem();
           if(sElem.isbPos())
           {
               ent.sCont.setPosList(sElem.getPosList());
               sElem.setbPos(false);
           }
           else
           {
               sElem.setPosList(ent.sCont.getPosList());
           }
           List<SenseElem> sContain = (List<SenseElem>) ent.sCont.getsContain();
           sContain.add(sElem);
       }
       
       if(qName.equalsIgnoreCase("entry"))
       {
           //add Entry object to DB
           if(ent.isbRead() && ent.isbMean())
           {
                ++count;
             
                //if(!ent.isbKanji())
                //{
                //if(ent.kElem == null)
                //{
                    //ent.kElem = new KanjiElem();
                //}

                if(ent.kElem.getKebs() == null)
                {
                    ent.kElem.setKebs(new ArrayList<>());  
                    ent.kElem.getKebs().add("No Kanji");
                }
               // }
                
                if(!ent.isbExamp())
                {
                    if(ent.sCont.getsElem().getExampleList() == null)
                    {
                        ent.sCont.getsElem().setExampleList(new ArrayList<>());
                    }
                    ent.sCont.getsElem().getExampleList().add("No Examples Yet.");
                }
                
                kanji = listToString(ent.kElem.getKebs());
                reading = listToString(ent.rElem.getRebs());
                //meaning = listToString(ent.sCont.getsContain().);
                //example = listToString(ent.exList);
                    
                System.out.println(count);
                //System.out.println("Kanji count is "+countK+", Size is: "+ent.kElem.getKebs().size());
                System.out.println(kanji);
                //System.out.println("Reading count is "+countR+", Size is: "+ent.rElem.getRebs().size());
                System.out.println(reading);
                //System.out.println("Sense count is "+countS+", Size is: "+ent.sCont.getsContain().size());
                
                for(int i=0; i<ent.sCont.getsContain().size(); ++i)
                {
                    int x = i+1;
                    SenseElem sElem = ent.sCont.getsContain().get(i);
                    System.out.println("Sense "+x);
                    System.out.println("Grammar Point(s):");
                    System.out.println(listToString(sElem.getPosList()));
                    System.out.println("Meaning(s):");
                    System.out.println(listToString(sElem.getMeaningList()));
                }
                //System.out.println("PoS is "+ent.getPos());
                //System.out.println("English count is "+countM);
                //System.out.println(meaning);
                //System.out.println(example);
                
                countK = 0;
                countR = 0;
                countM = 0;
                countS = 0;
                
                // commented out this stuff so DB doesn't get filled again accidentally
                em.getTransaction().begin();
                
                JERefs jer = new JERefs();
                try
                {
                    jer.setKana(ser.serialize(ent.rElem));
                    jer.setKanji(ser.serialize(ent.kElem));
                    jer.setSenses(ser.serialize(ent.sCont));
                }
                catch (IOException ex)
                {
                    Logger.getLogger(JEntryHandler.class.getName()).log(Level.SEVERE, "JEntryHandler.endElement: IOException", ex.toString());
                }
                em.persist(jer);
                em.getTransaction().commit();
           }
           //entList.add(ent);
       }
   }
   
   // if an element has characters between tags, this is called automatically by DefaultHandler
   @Override
   public void characters(char ch[], int start, int length) throws SAXException
   {
       String stuff = new String(ch, start, length);
       
       if(bKeb)
       {
           ent.kElem.getKebs().add(stuff);
           ++countK;
           bKeb = false;
           //System.out.println("Kanji Added: "+stuff);
       }
       else if(bKinf)
       {
           ent.kElem.getKeInfs().add(stuff);
           bKinf = false;
       }
       else if(bKpri)
       {
           ent.kElem.getKePris().add(stuff);
           bKpri = false;
       }
       else if(bReading)
       {
           ent.rElem.getRebs().add(stuff);
           ++countR;
           bReading = false;
       }
       else if(bRnoKan)
       {
           ent.rElem.getReNokanjis().add(stuff);
           bRnoKan = false;
       }
       else if(bRpri)
       {
           ent.rElem.getRePris().add(stuff);
           bRpri = false;
       }
       else if(bMeaning)
       {
           ent.sCont.getsElem().getMeaningList().add(stuff);
           ++countM;
           bMeaning = false;
       }
       else if(bExample)
       {
           ent.setbExamp(bExample);
           ent.sCont.getsElem().getExampleList().add(stuff);
           bExample = false;
       }
       else if(bPos)
       {
           ent.sCont.getsElem().getPosList().add(stuff);
           bPos = false;
       }
       else if(bAnton)
       {
           ent.sCont.getsElem().getAntonymList().add(stuff);
           bAnton = false;
       }
       else if(bDial)
       {
           ent.sCont.getsElem().getDialectList().add(stuff);
           bDial = false;
       }
       else if(bField)
       {
           ent.sCont.getsElem().getFieldList().add(stuff);
           bField = false;
       }
       
   }
   
    // Creates a CSV of a list of either the reading(s) or the meaning(s) of a Kanji Entry
    public String listToString(List<String> list)
    {
        // Using this aggregate stream as Iterator should only be used for removing
        String listCat = list.stream().collect(Collectors.joining("|"));
       
        return listCat;
    }
}
