select count(*) from domain;
-- 191,811    

select count(*) from `domain` where accession like 'PF%';
-- 9,318

select count(*) from `domain` where accession like 'PB%';
-- 182,493

select * from 
(
select count(*) as cnt, sd.domain_accession
from sequence_domain as sd
 inner join sequence as s on sd.sequence_accession = s.accession
group by sd.domain_accession
order by count(*) desc limit 25
) as a inner join domain as d on a.domain_accession = d.accession

 cnt     domain_accession     id               accession     description                                                         
 ------  -------------------  ---------------  ------------  ------------------------------------------------------------------- 
 68094   PF00516              GP120            PF00516       Envelope glycoprotein GP120                                         
 56860   PF00078              RVT_1            PF00078       Reverse transcriptase (RNA-dependent DNA polymerase)                
 46480   PF00077              RVP              PF00077       Retroviral aspartyl protease                                        
 45841   PF00115              COX1             PF00115       Cytochrome C and Quinol oxidase polypeptide I                       
 43596   PF00033              Cytochrom_B_N    PF00033       Cytochrome b(N-terminal)/b6/petB                                    
 40520   PF00005              ABC_tran         PF00005       ABC transporter                                                     
 30917   PF00361              Oxidored_q1      PF00361       NADH-Ubiquinone/plastoquinone (complex I), various chains           
 27861   PF00032              Cytochrom_B_C    PF00032       Cytochrome b(C-terminal)/b6/petD                                    
 24474   PF00069              Pkinase          PF00069       Protein kinase domain                                               
 24469   PF02518              HATPase_c        PF02518       Histidine kinase-, DNA gyrase B-, and HSP90-like ATPase             
 23334   PF00072              Response_reg     PF00072       Response regulator receiver domain                                  
 22942   PF07690              MFS_1            PF07690       Major Facilitator Superfamily                                       
 22270   PF01560              HCV_NS1          PF01560       Hepatitis C virus non-structural protein E2/NS1                     
 21982   PF06817              RVT_thumb        PF06817       Reverse transcriptase thumb domain                                  
 19183   PF00016              RuBisCO_large    PF00016       Ribulose bisphosphate carboxylase large chain, catalytic domain     
 18547   PF00528              BPD_transp_1     PF00528       Binding-protein-dependent transport system inner membrane component 
 18428   PF01539              HCV_env          PF01539       Hepatitis C virus envelope glycoprotein E1                          
 18133   PF00106              adh_short        PF00106       short chain dehydrogenase                                           
 18068   PF02788              RuBisCO_large_N  PF02788       Ribulose bisphosphate carboxylase large chain, N-terminal domain    
 14881   PF00271              Helicase_C       PF00271       Helicase conserved C-terminal domain                                
 14855   PF00009              GTP_EFTU         PF00009       Elongation factor Tu GTP binding domain                             
 14599   PF00001              7tm_1            PF00001       7 transmembrane receptor (rhodopsin family)                         
 14038   PF00512              HisKA            PF00512       His Kinase A (phosphoacceptor) domain                               
 13650   PF00126              HTH_1            PF00126       Bacterial regulatory helix-turn-helix protein, lysR family          
 13287   PF00583              Acetyltransf_1   PF00583       Acetyltransferase (GNAT) family               

(swissprot only)
cnt     domain_accession     id              accession     description                                                     
 ------  -------------------  --------------  ------------  --------------------------------------------------------------- 
 3775    PF00005              ABC_tran        PF00005       ABC transporter                                                 
 2653    PF00069              Pkinase         PF00069       Protein kinase domain                                           
 2435    PF00009              GTP_EFTU        PF00009       Elongation factor Tu GTP binding domain                         
 2414    PF03144              GTP_EFTU_D2     PF03144       Elongation factor Tu domain 2                                   
 2363    PF00001              7tm_1           PF00001       7 transmembrane receptor (rhodopsin family)                     
 2135    PF00271              Helicase_C      PF00271       Helicase conserved C-terminal domain                            
 1927    PF00096              zf-C2H2         PF00096       Zinc finger, C2H2 type                                          
 1908    PF00033              Cytochrom_B_N   PF00033       Cytochrome b(N-terminal)/b6/petB                                
 1889    PF00587              tRNA-synt_2b    PF00587       tRNA synthetase class II core domain (G, H, P, S and T)         
 1827    PF00032              Cytochrom_B_C   PF00032       Cytochrome b(C-terminal)/b6/petD                                
 1669    PF00400              WD40            PF00400       WD domain, G-beta repeat                                        
 1585    PF00046              Homeobox        PF00046       Homeobox domain                                                 
 1509    PF00006              ATP-synt_ab     PF00006       ATP synthase alpha/beta family, nucleotide-binding domain       
 1484    PF00156              Pribosyltran    PF00156       Phosphoribosyl transferase domain                               
 1434    PF02874              ATP-synt_ab_N   PF02874       ATP synthase alpha/beta family, beta-barrel domain              
 1421    PF00306              ATP-synt_ab_C   PF00306       ATP synthase alpha/beta chain, C terminal domain                
 1417    PF00036              efhand          PF00036       EF hand                                                         
 1347    PF00004              AAA             PF00004       ATPase family associated with various cellular activities (AAA) 
 1318    PF03129              HGTP_anticodon  PF03129       Anticodon binding domain                                        
 1313    PF01336              tRNA_anti       PF01336       OB-fold nucleic acid binding domain                             
 1252    PF00270              DEAD            PF00270       DEAD/DEAH box helicase                                          
 1220    PF00076              RRM_1           PF00076       RNA recognition motif. (a.k.a. RRM, RBD, or RNP domain)         
 1218    PF02518              HATPase_c       PF02518       Histidine kinase-, DNA gyrase B-, and HSP90-like ATPase         
 1214    PF01926              MMR_HSR1        PF01926       GTPase of unknown function                                      
 1179    PF00696              AA_kinase       PF00696       Amino acid kinase family 


select count(*), sd.domain_accession, d.id, d.description
from sequence_domain as sd 
    inner join domain as d on sd.domain_accession = d.accession
    inner join sequence as s on sd.sequence_accession = s.accession
where s.species = 32
group by sd.domain_accession, d.id, d.description
order by count(*) desc limit 25;

 count(*)     domain_accession     id           description                                               
 -----------  -------------------  -----------  --------------------------------------------------------- 
 2646         PF00129              MHC_I        Class I Histocompatibility antigen, domains alpha 1 and 2 
 1690         PF07654              C1-set       Immunoglobulin C1-set domain                              
 1551         PF00096              zf-C2H2      Zinc finger, C2H2 type                                    
 1399         PF00969              MHC_II_beta  Class II histocompatibility antigen, beta domain          
 1383         PF00001              7tm_1        7 transmembrane receptor (rhodopsin family)               
 1051         PF07686              V-set        Immunoglobulin V-set domain                               
 831          PF00069              Pkinase      Protein kinase domain                                     
 668          PF00047              ig           Immunoglobulin domain                                     
 627          PF00400              WD40         WD domain, G-beta repeat                                  
 604          PF01352              KRAB         KRAB box                                                  
 593          PF00169              PH           PH domain                                                 
 542          PF00023              Ank          Ankyrin repeat                                            
 522          PF00076              RRM_1        RNA recognition motif. (a.k.a. RRM, RBD, or RNP domain)   
 471          PF00361              Oxidored_q1  NADH-Ubiquinone/plastoquinone (complex I), various chains 
 470          PF00560              LRR_1        Leucine Rich Repeat                                       
 461          PF00041              fn3          Fibronectin type III domain                               
 438          PF00097              zf-C3HC4     Zinc finger, C3HC4 type (RING finger)                     
 435          PF00018              SH3_1        SH3 domain                                                
 389          PF00046              Homeobox     Homeobox domain                                           
 386          PF00036              efhand       EF hand                                                   
 379          PF00008              EGF          EGF-like domain                                           
 374          PF00089              Trypsin      Trypsin                                                   
 350          PF07679              I-set        Immunoglobulin I-set domain                               
 315          PF07714              Pkinase_Tyr  Protein tyrosine kinase                                   
 314          PF00168              C2           C2 domain                                      

select count(*), sd.domain_accession, d.id, d.description
from sequence_domain as sd 
    inner join domain as d on sd.domain_accession = d.accession
    inner join drug_target as dt on dt.sequence_accession = sd.sequence_accession
    inner join drug as d2 on dt.drug_id = d2.id
where d2.is_approved = 1
group by sd.domain_accession, d.id, d.description
order by count(*) desc limit 25;

 count(*)     domain_accession     id              description                                              
 -----------  -------------------  --------------  -------------------------------------------------------- 
 828          PF00001              7tm_1           7 transmembrane receptor (rhodopsin family)              
 195          PF00105              zf-C4           Zinc finger, C4 type (two domains)                       
 184          PF00104              Hormone_recep   Ligand-binding domain of nuclear hormone receptor        
 158          PF02931              Neur_chan_LBD   Neurotransmitter-gated ion-channel ligand binding domain 
 157          PF02932              Neur_chan_memb  Neurotransmitter-gated ion-channel transmembrane region  
 156          PF00047              ig              Immunoglobulin domain                                    
 144          PF00008              EGF             EGF-like domain                                          
 128          PF00520              Ion_trans       Ion transport protein                                    
 117          PF00273              Serum_albumin   Serum albumin family                                     
 103          PF00089              Trypsin         Trypsin                                                  
 88           PF01391              Collagen        Collagen triple helix repeat (20 copies)                 
 87           PF00041              fn3             Fibronectin type III domain                              
 83           PF00209              SNF             Sodium:neurotransmitter symporter family                 
 83           PF07714              Pkinase_Tyr     Protein tyrosine kinase                                  
 80           PB000086             Pfam-B_86       (null)                                                   
 74           PF01094              ANF_receptor    Receptor family ligand binding region                    
 73           PF03098              An_peroxidase   Animal haem peroxidase                                   
 67           PF00005              ABC_tran        ABC transporter                                          
 64           PF00067              p450            Cytochrome P450                                          
 64           PF00155              Aminotran_1_2   Aminotransferase class I and II                          
 60           PF00039              fn1             Fibronectin type I domain                                
 59           PF00060              Lig_chan        Ligand-gated ion channel                                 
 58           PB009479             Pfam-B_9479     (null)                                                   
 57           PF00386              C1q             C1q domain                                               
 57           PF00905              Transpeptidase  Penicillin binding protein transpeptidase domain           

select count(*), sd.domain_accession, d.id, d.description
from sequence_domain as sd 
    inner join domain as d on sd.domain_accession = d.accession
    inner join drug_target as dt on dt.sequence_accession = sd.sequence_accession
    --inner join sequence as s on sd.sequence_accession = s.accession
    --where s.species = 32
group by sd.domain_accession, d.id, d.description
order by count(*) desc limit 25;

 count(*)     domain_accession     id              description                                              
 -----------  -------------------  --------------  -------------------------------------------------------- 
 867          PF00001              7tm_1           7 transmembrane receptor (rhodopsin family)              
 308          PF00105              zf-C4           Zinc finger, C4 type (two domains)                       
 308          PF00089              Trypsin         Trypsin                                                  
 284          PF00104              Hormone_recep   Ligand-binding domain of nuclear hormone receptor        
 241          PF00069              Pkinase         Protein kinase domain                                    
 198          PF00008              EGF             EGF-like domain                                          
 174          PF00047              ig              Immunoglobulin domain                                    
 160          PF02931              Neur_chan_LBD   Neurotransmitter-gated ion-channel ligand binding domain 
 160          PF07714              Pkinase_Tyr     Protein tyrosine kinase                                  
 158          PF02932              Neur_chan_memb  Neurotransmitter-gated ion-channel transmembrane region  
 141          PF00175              NAD_binding_1   Oxidoreductase NAD-binding domain                        
 141          PF00520              Ion_trans       Ion transport protein                                    
 139          PF00106              adh_short       short chain dehydrogenase                                
 138          PF00067              p450            Cytochrome P450                                          
 136          PF00273              Serum_albumin   Serum albumin family                                     
 127          PF00258              Flavodoxin_1    Flavodoxin                                               
 124          PF01094              ANF_receptor    Receptor family ligand binding region                    
 118          PF00155              Aminotran_1_2   Aminotransferase class I and II                          
 115          PF00667              FAD_binding_1   FAD binding domain                                       
 113          PF02518              HATPase_c       Histidine kinase-, DNA gyrase B-, and HSP90-like ATPase  
 111          PF00060              Lig_chan        Ligand-gated ion channel                                 
 107          PF00194              Carb_anhydrase  Eukaryotic-type carbonic anhydrase                       
 107          PF02898              NO_synthase     Nitric oxide synthase, oxygenase domain                  
 105          PF01391              Collagen        Collagen triple helix repeat (20 copies)                 
 104          PF00041              fn3             Fibronectin type III domain                              


select count(*) from architecture;
 count(*)    
 ----------- 
 235848      


select count(*), architecture_id
from sequence_architecture
group by architecture_id
order by count(*) desc limit 10;
 count(*)     architecture_id    
 -----------  ------------------ 
 62834        758                
 45451        290                
 26556        222                
 20712        759                
 20501        189                
 20426        403                
 18925        5147               
 17146        251                
 16892        299                
 16341        287     

select a.cnt, a.architecture_id, b.domains, d.id, d.description from
(select count(*) as cnt, architecture_id
from sequence_architecture
group by architecture_id
order by count(*) desc limit 25) as a inner join architecture as b on a.architecture_id = b.id
left join domain as d on b.domains = d.accession

 cnt     architecture_id     domains          id              description                                                         
 ------  ------------------  ---------------  --------------  ------------------------------------------------------------------- 
 62834   758                 PF00516          GP120           Envelope glycoprotein GP120                                         
 45451   290                 PF00115          COX1            Cytochrome C and Quinol oxidase polypeptide I                       
 26556   222                 PF00033.PF00032  (null)          (null)                                                              
 20712   759                 PF00077          RVP             Retroviral aspartyl protease                                        
 20501   189                 PF00005          ABC_tran        ABC transporter                                                     
 20426   403                 PF07690          MFS_1           Major Facilitator Superfamily                                       
 18925   5147                PF00078          RVT_1           Reverse transcriptase (RNA-dependent DNA polymerase)                
 17146   251                 PF02788.PF00016  (null)          (null)                                                              
 16892   299                 PF00033          Cytochrom_B_N   Cytochrome b(N-terminal)/b6/petB                                    
 16341   287                 PF01539.PF01560  (null)          (null)                                                              
 14429   611                 PF00528          BPD_transp_1    Binding-protein-dependent transport system inner membrane component 
 14092   1368                PF00069          Pkinase         Protein kinase domain                                               
 13557   79                  PF00106          adh_short       short chain dehydrogenase                                           
 12688   505                 PF00126.PF03466  (null)          (null)                                                              
 11804   124                 PF00001          7tm_1           7 transmembrane receptor (rhodopsin family)                         
 10793   5222                PF00077.PF00078  (null)          (null)                                                              
 10192   310                 PF01824.PF01348  (null)          (null)                                                              
 10148   218                 PF00361          Oxidored_q1     NADH-Ubiquinone/plastoquinone (complex I), various chains           
 9977    291                 PF02790.PF00116  (null)          (null)                                                              
 9377    468                 PF00583          Acetyltransf_1  Acetyltransferase (GNAT) family                                     
 9209    240                 PF00509          Hemagglutinin   Hemagglutinin                                                       
 8493    276                 PF00361.PF06444  (null)          (null)                                                              
 7484    289                 PF00146          NADHdh          NADH dehydrogenase                                                  
 7348    4987                PF00469          F-protein       Negative factor, (F-Protein) or Nef                                 
 7101    678                 PF00561          Abhydrolase_1   alpha/beta hydrolase fold  

 
select a.cnt, a.architecture_id, b.domains, d.id, d.description from
(select count(*) as cnt, architecture_id
from sequence_architecture inner join sequence as s on sequence_accession = s.accession
where s.species = 32
group by architecture_id
order by count(*) desc limit 25) as a inner join architecture as b on a.architecture_id = b.id
left join domain as d on b.domains = d.accession

cnt     architecture_id     domains                  id               description                                               
 ------  ------------------  -----------------------  ---------------  --------------------------------------------------------- 
 1777    821                 PF00129                  MHC_I            Class I Histocompatibility antigen, domains alpha 1 and 2 
 1071    2134                PF00969                  MHC_II_beta      Class II histocompatibility antigen, beta domain          
 948     124                 PF00001                  7tm_1            7 transmembrane receptor (rhodopsin family)               
 612     352                 PF07686                  V-set            Immunoglobulin V-set domain                               
 559     5136                PF00129.PF07654          (null)           (null)                                                    
 322     1368                PF00069                  Pkinase          Protein kinase domain                                     
 279     222                 PF00033.PF00032          (null)           (null)                                                    
 263     7565                PF00969.PF07654          (null)           (null)                                                    
 239     2096                PF00089                  Trypsin          Trypsin                                                   
 237     20                  PF00129.PF07654.PF06623  (null)           (null)                                                    
 226     220                 PF00119                  ATP-synt_A       ATP synthase A chain                                      
 214     277                 PF00067                  p450             Cytochrome P450                                           
 212     2201                PF00071                  Ras              Ras family                                                
 171     377                 PF07654                  C1-set           Immunoglobulin C1-set domain                              
 158     295                 PF00662.PF00361.PF06455  (null)           (null)                                                    
 149     342                 PF00022                  Actin            Actin                                                     
 132     63                  PF00993.PF07654          (null)           (null)                                                    
 131     2198                PF00076                  RRM_1            RNA recognition motif. (a.k.a. RRM, RBD, or RNP domain)   
 127     276                 PF00361.PF06444          (null)           (null)                                                    
 125     38034               PB000008.PF00001         (null)           (null)                                                    
 122     368                 PF03414                  Glyco_transf_6   Glycosyltransferase family 6                              
 120     289                 PF00146                  NADHdh           NADH dehydrogenase                                        
 119     292                 PF00510                  COX3             Cytochrome c oxidase subunit III                          
 117     1691                PF00909                  Ammonium_transp  Ammonium Transporter Family                               
 114     751                 PF00046                  Homeobox         Homeobox domain                                           

 select a.cnt, a.architecture_id, b.domains, d.id, d.description from
(select count(*) as cnt, architecture_id
from sequence_architecture as sa 
inner join drug_target as dt on dt.sequence_accession = sa.sequence_accession
inner join drug as d2 on dt.drug_id = d2.id
where d2.is_approved = 1
group by architecture_id
order by count(*) desc limit 25) as a inner join architecture as b on a.architecture_id = b.id
left join domain as d on b.domains = d.accession

 cnt     architecture_id     domains                                    id              description                                 
 ------  ------------------  -----------------------------------------  --------------  ------------------------------------------- 
 266     124                 PF00001                                    7tm_1           7 transmembrane receptor (rhodopsin family) 
 78      135                 PF02931.PF02932                            (null)          (null)                                      
 58      17718               PB009479.PF00273.PF00273.PF00273           (null)          (null)                                      
 58      17486               PF00273.PF00273.PF00273                    (null)          (null)                                      
 55      277                 PF00067                                    p450            Cytochrome P450                             
 51      17318               PF00001.PB012994.PB019520                  (null)          (null)                                      
 49      24667               PB012862.PF00001                           (null)          (null)                                      
 47      55562               PF00912.PB000195.PF00905                   (null)          (null)                                      
 46      27564               PB059804.PF02931.PF02932                   (null)          (null)                                      
 46      1432                PF00194                                    Carb_anhydrase  Eukaryotic-type carbonic anhydrase          
 42      22                  PF00155                                    Aminotran_1_2   Aminotransferase class I and II             
 39      56096               PF00008.PF03098.PF03098.PB008862           (null)          (null)                                      
 38      11770               PF01391.PF00386                            (null)          (null)                                      
 38      26414               PF00047.PF00047.PB032155                   (null)          (null)                                      
 35      130                 PB020830.PF00001.PB020826                  (null)          (null)                                      
 33      81947               PB014539.PF00209                           (null)          (null)                                      
 32      25752               PF02159.PF00105.PB011345.PB000636.PF00104  (null)          (null)                                      
 32      17444               PF00001.PB029362                           (null)          (null)                                      
 31      17446               PB011799.PF00001.PB009232                  (null)          (null)                                      
 31      17319               PB027813.PF00001                           (null)          (null)                                      
 30      436                 PF00266                                    Aminotran_5     Aminotransferase class-V                    
 30      56094               PF00008.PF03098                            (null)          (null)                                      
 29      17332               PB027866.PF00001                           (null)          (null)                                      
 29      24666               PF00001.PB011049                           (null)          (null)                                      
 28      215872              PB028418.PF03491.PF00209                   (null)          (null)                                      




-- number of distinct domains
select count(distinct domain_accession) from sequence_domain as sd 
inner join drug_target as dt on dt.sequence_accession = sd.sequence_accession
inner join drug as d on dt.drug_id = d.id
where d.is_approved = 1

-- defined in pfam
191,811
-- all genomes
192,011 (200 more?? - sequences accessions that have been merged)
-- human genome
28,053
-- drug targets
2,724

-- number of distinct architectures
select count(distinct architecture_id) from sequence_architecture as sa
    inner join drug_target as dt on dt.sequence_accession = sa.sequence_accession 
inner join drug as d on dt.drug_id = d.id
where d.is_approved = 1

-- defined in pfam
235,848
-- all genomes
235,848
-- human genomes
20,826
-- drug targets
1337
);


select count(distinct sd.sequence_accession) 
from sequence_domain as sd inner join drug_target as s on sd.sequence_accession = s.sequence_accession

 3126379
44901


select count(distinct sd.sequence_accession) 
from sequence_architecture as sd inner join drug_target as s on sd.sequence_accession = s.sequence_accession

 3126379 
44901



-- most promiscuous domains in architectures
select t1.*, t2.id, t2.description from 
(select count(distinct architecture_id), domain_accession 
from domain_architecture 
group by domain_accession 
order by count(*) desc limit 10) as t1 inner join domain as t2 on t1.domain_accession = t2.accession


-- occurrences of domains in all architectures, repeats within architecture included
945,347

-- occurrences of domains in all architectures, repeats within architecture ignored
741,175

-- 10 most common domains in architectures, repeats within architecture included
 count(*)     domain_accession     id               description                                      
 -----------  -------------------  ---------------  ------------------------------------------------ 
 21056        PF00560              LRR_1            Leucine Rich Repeat                              
 12204        PF00096              zf-C2H2          Zinc finger, C2H2 type                           
 11990        PF00515              TPR_1            Tetratricopeptide repeat                         
 11153        PF00023              Ank              Ankyrin repeat                                   
 8038         PF00400              WD40             WD domain, G-beta repeat                         
 5683         PF00041              fn3              Fibronectin type III domain                      
 5307         PF00353              HemolysinCabind  Hemolysin-type calcium-binding repeat (2 copies) 
 4974         PF00008              EGF              EGF-like domain                                  
 4955         PF01535              PPR              PPR repeat                                       
 4786         PF07679              I-set            Immunoglobulin I-set domain    

-- 10 most common domains in architectures, repeats within architecture ignored
select * from (
select b.domain_accession, sum(cnt)from domain_architecture as b
inner join (
select count(*) as cnt, t1.architecture_id
from sequence_architecture as t1
group by t1.architecture_id) as c on b.architecture_id = c.architecture_id
group by domain_accession order by sum(cnt) desc limit 10 ) as t2 inner join domain as t3
on t2.domain_accession = t3.accession

 cnt     architecture_id    
 ------  ------------------ 
 25      1                  
 1530    2                  
 7       3                  
 94      4                  
 13      5                  
 51      6                  
 1496    7                  
 3       8                  
 410     9   

 count(distinct architecture_id)     domain_accession     id               description                                      
 ----------------------------------  -------------------  ---------------  ------------------------------------------------ 
 3115                                PF00560              LRR_1            Leucine Rich Repeat                              
 2000                                PF00096              zf-C2H2          Zinc finger, C2H2 type                           
 2809                                PF00515              TPR_1            Tetratricopeptide repeat                         
 1827                                PF00023              Ank              Ankyrin repeat                                   
 2153                                PF00400              WD40             WD domain, G-beta repeat                         
 1407                                PF00041              fn3              Fibronectin type III domain                      
 442                                 PF00353              HemolysinCabind  Hemolysin-type calcium-binding repeat (2 copies) 
 1227                                PF00008              EGF              EGF-like domain                                  
 574                                 PF01535              PPR              PPR repeat                                       
 981                                 PF07679              I-set            Immunoglobulin I-set domain  

-- 10 most common domains in architectures from human sequences, repeats within architecture ignored

select * from (
select b.domain_accession, sum(a.cnt) from domain_architecture as b
inner join (select count(*) as cnt, t1.architecture_id
from sequence_architecture as t1
    inner join `sequence` as t2 on t1.sequence_accession = t2.accession
where t2.species = 32 group by t1.architecture_id) as a on b.architecture_id = a.architecture_id
group by b.domain_accession
order by sum(a.cnt) desc
limit 10 ) as c inner join domain as d on c.domain_accession = d.accession



-- drug analysis

-- total number of drugs
select count(*) from drug
4765

-- total number of approved drugs
select count(*) from drug where is_approved = 1
1485

-- total number of targets of all drugs
select count(*) from drug_target
12040

-- total number of targets of approved drugs
select count(*) from drug_target as dt inner join drug as d on dt.drug_id = d.id
where d.is_approved = 1
4500

-- total number of targets of approved drugs with human targets
select count(distinct dt.sequence_accession) 
from drug_target as dt 
    inner join drug as d on dt.drug_id = d.id
    inner join `sequence` as s on dt.sequence_accession = s.accession
where d.is_approved = 1 and s.species = 32
4027

-- total number of target architectures of approved drugs with human targets
select count(distinct sa.architecture_id) 
from drug_target as dt 
    inner join drug as d on dt.drug_id = d.id
    inner join `sequence` as s on dt.sequence_accession = s.accession
    inner join sequence_architecture as sa on s.accession = sa.sequence_accession
where d.is_approved = 1 and s.species = 32
1149



select count(*)
from drug_target as dt 
    inner join drug as d on dt.drug_id = d.id
    inner join `sequence` as s on dt.sequence_accession = s.accession
    inner join sequence_architecture as sa on dt.sequence_accession = sa.sequence_accession
where d.is_approved = 1 and s.species = 32
-- approved drugs 4626 human targets and their architectures

--create table tmp1 (architecture_id int, sequence_accession varchar(10), cnt int)

-- insert into tmp1
select sa.architecture_id, dt.sequence_accession, count(*) as cnt
from drug_target as dt 
    inner join drug as d on dt.drug_id = d.id
    inner join `sequence` as s on dt.sequence_accession = s.accession
    inner join sequence_architecture as sa on s.accession = sa.sequence_accession
where d.is_approved = 1 and s.species = 32
group by sa.architecture_id, dt.sequence_accession










