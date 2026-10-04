// JRDFox(c) Copyright University of Oxford, 2013. All Rights Reserved.
package tech.oxfordsemantic.jrdfox;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import tech.oxfordsemantic.jrdfox.client.ConnectionFactory;
import tech.oxfordsemantic.jrdfox.client.Cursor;
import tech.oxfordsemantic.jrdfox.client.DataStoreConnection;
import tech.oxfordsemantic.jrdfox.client.ResourceValue;
import tech.oxfordsemantic.jrdfox.client.ServerConnection;
import tech.oxfordsemantic.jrdfox.client.UpdateType;
import tech.oxfordsemantic.jrdfox.exceptions.JRDFoxException;

public class JRDFoxDemo {

    public static void main(String[] args) throws Exception {
        // This example shows how to instantiate JRDFox in Java, load an RDF file in Turtle, evaluate
        // a query, perform reasoning (i.e., extend the set of triples with all implicit ones), and
        // then re-evaluate the query (and get more results as a the result of reasoning).

        // Unlike most RDF stores, JRDFox currently maintains a separation between RDF data and the
        // ontology. That is, the data (i.e., the ABox) and the ontology (i.e., the TBox) are currently
        // best maintained in separate files. This is quite different to many (most?) existing RDF
        // stores, where both the ontology and the data are kept in a single file. Thus, in this\
        // example we will use two kinds of axioms for reasoning:
        //
        // - axioms from an OWL ontology stored in file called univ-bench.owl, and
        //
        // - manually created rules written in a format proprietary to RDFox and stored in a file
        //   called additional-rules.txt.
        //
        // The data will be loaded from a file called lubm1.ttl. All files are located in the
        // source directory and will be loaded through Java class loaders -- please refer to Java
        // documentation for more details.

        // In this example, the rules are kept in a file separate from the ontology. JRDFox supports
        // SWRL rules; thus, it is possible to store the rules into the OWL ontology. However, JRDFox
        // does not (yet) support SWRL built-in predicates, so any rules involving built-in predicates
        // should be written in the native format of RDFox. The format of the rules should be obvious
        // from the example. Built-in functions are invoked using the BIND and FILTER syntax from
        // SPARQL, and most SPARQL built-in functions are supported.

        // We first load an OWL ontology. This is done using the OWL API -- a well-known API for
        // manipulating OWL ontologies. The details of how to use OWL API are out of scope of this
        // example; please refer to http://owlapi.sourceforge.net/ for more information. Since the
        // ontology is loaded through the OWL API, it can be in any format (i.e., not only Turtle).
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(JRDFoxDemo.class.getResource("data/univ-bench.owl")));

        // We now connect to the local server. The local server can be started explicitly using
        // ConnectionFactory.startLocalServer(), but it will be automatically started when the first
        // connection to it has been made. Our goal is obtain a connection to a data store, but before
        // this can be done, the data store must be created. Hence, we will connect to the server
        // first as server connections allow manipulating data stores. All connections should be closed
        // once they are not to be used any more in order to prevent resource leaks. Server and data
        // store connections implement the AutoCloseable interface so they can be used in
        // try-with-resources statements. By using "rdfox:local" as the server name, we indicate that
        // we wish to access the local server. At present, the user name and password are ignored.
        try (ServerConnection serverConnection = ConnectionFactory.newServerConnection("rdfox:local", "", "")) {

            // We create a data store of type "par-complex-nn".
            serverConnection.createDataStore("example", "par-complex-nn", Collections.emptyMap());

            // We connect to the data store.
            try (DataStoreConnection dataStoreConnection = serverConnection.newDataStoreConnection("example")) {
                // We next specify how many threads the store should use during import of data and reasoning.
                // This method is only applicable for stores NarrowParallelHead and WideParallelHead
                System.out.println("TODO: Not Setting the number of threads...");
                //dataStoreConnection.setNumberOfThreads(2);

                // We next import the RDF data into the store. At present, only Turtle/N-triples files are supported.
                // At the moment, please convert RDF/XML files into Turtle format to load into JRDFox.
                System.out.println("Importing RDF data...");
                try (InputStream inputStream = new BufferedInputStream(JRDFoxDemo.class.getResourceAsStream("data/lubm1.ttl"))) {
                    dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, inputStream);
                }

                // RDFox manages data in several domains.
                //
                // - EDB are the explicitly stated facts.
                //
                // - IDB facts are the EDB facts plus all of their consequences. This is what normally should be
                //   queried -- that is, these are the "current" facts in the store.
                //
                // - IDBrep is different from IDB only if optimized equality reasoning is used. In that case, RDFox
                //   will select for each set of equal resources one representative, and IDBrep then consists of the
                //   IDB facts that contain just the representative resources.
                //
                // - IDBrepNoEDB is equal IDBrep minus EDB.
                //
                // The domain must be specified in various places where queries are evaluated. If a query domain is not
                // specified, the IDB domain is used.
                System.out.println("Number of tuples after import: " + getTriplesCount(dataStoreConnection));

                // SPARQL queries can be evaluated in several ways. One option is to have the query result be written to
                // an output stream in one of the supported formats.
                dataStoreConnection.evaluateStatement(Prefixes.s_emptyPrefixes, "SELECT DISTINCT ?Y WHERE { ?X ?Y ?Z }", Collections.emptyMap(), System.out, "application/sparql-results+json");

                // We now add the ontology and the custom rules to the data.
                System.out.println("Adding the ontology to the store...");
                //dataStoreConnection.importData(UpdateType.ADDITION, ontology, true, true, false, false);
                dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, ontology.toString());
                System.out.println("Importing rules from a file...");
                try (InputStream inputStream = new BufferedInputStream(JRDFoxDemo.class.getResourceAsStream("data/additional-rules.txt"))) {
                    dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, inputStream);
                }
                System.out.println("Number of tuples after materialization: " + getTriplesCount(dataStoreConnection));

                // We now evaluate the same query as before, but we do so using a cursor, which provides us with
                // programmatic access to individual query results. Cursors come in two guises. A live cursor is created
                // when a query is compiled on a connection with an active transaction. Answers to a live cursor are produced
                // as the cursor is being iterated over, which is beneficial as it does not use any memory for storing
                // query results. Moreover, a live cursor can be reopened many times, and each time its results will
                // reflect the current contents of a data store. In contrast, a frozen cursor is created when a query is
                // compiled on a connection without an active transaction. Then, a query is evaluated fully and its results
                // are stored in a temporary buffer before they can be accessed. If a frozen cursor is reopened, it will
                // still provide the same answer as at the time it was created. Since in our example no transaction is active
                // on a connection, the following statement creates a frozen cursor.
                try (Cursor cursor = dataStoreConnection.createCursor(Prefixes.s_emptyPrefixes, "SELECT DISTINCT ?Y WHERE { ?X ?Y ?Z }", Collections.emptyMap())) {
                    int numberOfRows = 0;
                    System.out.println();
                    System.out.println("=======================================================================================");
                    int arity = cursor.getArity();
                    // We iterate trough the result tuples
                    for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance()) {
                        // We iterate trough the terms of each tuple
                        for (int termIndex = 0; termIndex < arity; ++termIndex) {
                            if (termIndex != 0)
                                System.out.print("  ");
                            // For each term we get a Resource object that contains the lexical form and the data type of the term.
                            // One can also access terms as GroundTerm objects from the tech.oxfordsemantic.jrdfox.logic package using
                            // the method Cursor.getGroundTerm(int termIndex). Using objects from the tech.oxfordsemantic.jrdfox.logic
                            // package has the benefit of ensuring that at any point each term is represented by at most one Java
                            // object. This benefit, however, comes at a price, since, unlike in the case of Resource objects, the
                            // creation of GroundTerm objects involves a hash table lookup, which in some cases can lead to a significant
                            // overhead.
                            ResourceValue resource = cursor.getResourceValue(termIndex);
                            System.out.print(resource.toString(Prefixes.s_defaultPrefixes));
                        }
                        System.out.print(" * ");
                        System.out.print(multiplicity);
                        System.out.println();
                        ++numberOfRows;
                    }
                    // Since the iterator is exhausted, it does not need to be closed.
                    System.out.println("---------------------------------------------------------------------------------------");
                    System.out.println("  The number of rows returned: " + numberOfRows);
                    System.out.println("=======================================================================================");
                    System.out.println();
                }

                // RDFox supports incremental reasoning. One can import facts into the store incrementally by
                // calling DataStoreConnection.importDataFiles() with additional argument UpdateType.ADDITION.
                System.out.println("Import triples for incremental reasoning");
                try (InputStream inputStream = new BufferedInputStream(JRDFoxDemo.class.getResourceAsStream("data/lubm1-new.ttl"))) {
                    dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, inputStream);
                }
                // Adding the rules/facts changes the number of triples. Note that the store is updated incrementally.
                System.out.println("Number of tuples after addition: " + getTriplesCount(dataStoreConnection));
                // One can export the facts from the current store into a file as follows
                File finalFactsFile = File.createTempFile("final-facts", ".ttl");
                System.out.print("Exporting facts to file '" + finalFactsFile + "' ... ");
                try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(finalFactsFile))) {
                	Map<String, String> parameters = new HashMap<String, String>();
                    dataStoreConnection.exportData(Prefixes.s_defaultPrefixes, outputStream , "application/n-triples", parameters);
                }
                System.out.println("done.");
            }
        }
        System.out.println("This is the end of the example!");
    }

    protected static Map<String, String> getParameters(String... keyValuePairs) {
        Map<String, String> parameters = new HashMap<String, String>();
        for (int index = 0; index < keyValuePairs.length; index += 2)
            parameters.put(keyValuePairs[index], keyValuePairs[index + 1]);
        return parameters;
    }

    protected static long getTriplesCount(DataStoreConnection dataStoreConnection) throws JRDFoxException {
        HashMap<String, String> parameters = new HashMap<String, String>();
//        parameters.put("domain", queryDomain.toString());
        try (Cursor cursor = dataStoreConnection.createCursor(Prefixes.s_emptyPrefixes, "SELECT ?X ?Y ?Z WHERE{ ?X ?Y ?Z }", parameters)) {
            long result = 0;
            for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance())
                result += multiplicity;
            // Since the iterator is exhausted, it does not need to be closed.
            return result;
        }
    }
}
