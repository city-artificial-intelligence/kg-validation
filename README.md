# Ontology Services Toolkit

This branck uses the latest version of RDFox, which requires a special [license](https://www.cs.ox.ac.uk/isg/tools/RDFox/).

This toolkit currently included services for ontology projection and ontology constraint checking [1]. 

This toolkit relies on RDFox which is platform dependent. 
1. In the lib folder, the different pre-compiled libraries for RDFox are provided.
2. Follow the instructions in lib/mvn_install_jrdfox.txt to install the RDFox library in the local maven repository.

The ontology projection is being used by the [OptiqueVQS system](https://gitlab.com/ernesto.jimenez.ruiz/OptiqueVQS) [2,4], the [SIRIUS Geoscience image annotation and classification project](https://github.com/Sirius-sfi/geoscience-image-classification/tree/master/geoscience-image-classification), and the [OWL2Vec framework](https://gitlab.com/oholter/owl2vec). It also inspired the ontology graph projection used in [3]. 



## Related Publications

[1] Evgeny Kharlamov, Bernardo Cuenca Grau, Ernesto Jiménez-Ruiz, Steffen Lamparter, Gulnar Mehdi, Martin Ringsquandl, Yavor Nenov, Stephan Grimm, Mikhail Roshchin and Ian Horrocks. **Capturing Industrial Information Models with Ontologies and Constraints**. International Semantic Web Conference. 2016. ([PDF](http://www.cs.ox.ac.uk/files/8278/ISWC16_ModelManager.pdf))

[2] Ahmet Soylu, Evgeny Kharlamov, Dimitry Zheleznyakov, Ernesto Jimenez Ruiz, Martin Giese, Martin G. Skjaeveland, Dag Hovland, Rudolf Schlatte, Sebastian Brandt, Hallstein Lie, Ian Horrocks. **OptiqueVQS: a Visual Query System over Ontologies for Industry**. Semantic Web Journal, 2018. ([link](http://semantic-web-journal.net/content/optiquevqs-visual-query-system-over-ontologies-industry-0))

[3] Asan Agibetov, Ernesto Jiménez-Ruiz, Marta Ondresik, Alessandro Solimando, Imon Banerjee, Giovanna Guerrini, Chiara Eva Catalano, Joaquim Miguel Oliveira, Giuseppe Patanè, Rui Luís Reis, Michela Spagnuolo. **Supporting shared hypothesis testing in the biomedical domain**. J. Biomedical Semantics 9(1): 9:1-9:22, 2018. ([link](https://jbiomedsem.biomedcentral.com/articles/10.1186/s13326-018-0177-x))

[4] Vidar N. Klungre, Ahmet Soylu, Ernesto Jiménez-Ruiz, Evgeny Kharlamov, Martin Giese **Query Extension Suggestions for Visual Query Systems Through Ontology Projection and Indexing**. New Generation Comput. 37(4): 361-392. 2019 ([link](https://openaccess.city.ac.uk/id/eprint/22926/))

[5] Ole Magnus Holter, Erik Bryhn Myklebust, Chen Jiaoyan and Ernesto Jimenez-Ruiz. **Embedding OWL ontologies with OWL2Vec**. International Semantic Web Conference. Poster & Demos. 2019. ([PDF](https://www.cs.ox.ac.uk/isg/TR/OWL2vec_iswc2019_poster.pdf))