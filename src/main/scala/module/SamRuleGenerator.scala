package metarules.module

import metarules.meta._
import syntax._, Network._, Flags._, Flag._, RotFlip._, Implicits._, group.SymGroup._
import scala.collection.mutable.Buffer
import NetworkProperties._


class SamRuleGenerator(var context: RuleTransducer.Context) extends RuleGenerator with Adjacencies {

  def start(): Unit = {
    /*
    Generate OxO rules by iteration over list of supported crossings
    */
    val SamNetworks = List(Sam2, Sam3, Sam4, Sam5, Sam6, Sam7, Sam8, Sam9, Sam10, Sam11)

    val CrossNetworks = List(Road, Avenue, Onewayroad,
    Rail, Lightrail, Monorail, Glr1, Glr2/*, Str, Dirtroad, Rhw3, Mis, Rhw4, Rhw6s, Rhw8sm, Rhw8s, Rhw10s, Rhw12s, Rhw6cm,
    Rhw6c, Rhw8c, L1Rhw2, L1Rhw3, L1Mis, L1Rhw4, L1Rhw6s, L1Rhw8sm, L1Rhw8s, L1Rhw10s, L1Rhw12s, L1Rhw6cm,
    L1Rhw6c, L1Rhw8c, L2Rhw2, L2Rhw3, L2Mis, L2Rhw4, L2Rhw6s, L2Rhw8sm, L2Rhw8s, L2Rhw10s, L2Rhw12s, L2Rhw6cm,
    L2Rhw6c, L2Rhw8c, L3Mis, L3Rhw4, L3Rhw6s, L4Mis, L4Rhw4, L4Rhw6s*/, Tla3, Ave2, Ard3, Owr1, Owr3, Nrd4/*, Tla5, Owr4, 
	Owr5, Rd4, Rd6, Ave6, Tla7m, Ave6m*/)


    for (main <- SamNetworks; base <- main.base) {

      Rules += main~WE | (base ~> main)~WE~EW      // ortho
      Rules += main~WE | base~CW | % | main~CW  // ortho stub
      Rules += main~SE~ES | (base ~> main)~WN~NW   // diagonal
      Rules += main~SE~ES | (base ~> main)~(3,0,0,0)   // diagonal stub
      Rules += main~SE~ES | (base ~> main)~(1,0,0,0)   // diagonal stub alt?
      Rules += main~NE~EN | (base ~> main)~(1,0,0,0)   // diagonal stub
      Rules += main~NE~EN | (base ~> main)~(3,0,0,0)   // diagonal stub alt?
   
      Rules += main~WE | (base ~> main)~(2,0,11,0)	// orth to orth-diag bottom
      Rules += main~(2,0,11,0) | (base ~> main)~(11,3,0,0) // orth-diag bottom to orth-diag top
      Rules += main~(2,0,11,0) | (base ~> main)~(1,3,0,0) // orth-diag bottom to orth-diag top fix 1
      Rules += main~(2,0,2,0) | (base ~> main)~(11,3,0,0) // orth-diag bottom to orth-diag top fix 2
      Rules += main~(2,0,2,0) | (base ~> main)~(1,3,0,0) // orth-diag bottom to orth-diag top fix 3
	  Rules += main~(2,0,11,0) | base~(2,13,0,0) | % | main~(11,3,0,0) // orth-diag bottom to orth-diag top fix 4 (or is it this one?)
      Rules += main~(0,0,1,13) | (base ~> main)~NW~WN // orth-diag top to diag
	  Rules += main~(0,0,1,13) | base~(11,3,0,0) | % | main~(1,3,0,0) // orth-diag top to diag fix
      Rules += main~(0,0,1,13) | (base ~> main)~(3,0,0,0) // orth-diag top to diag stub
      Rules += main~(0,0,1,13) | (base ~> main)~(3,0,0,0) // orth-diag top to diag stub
	     
      Rules += main~SE~ES | (base ~> main)~(1,13,0,0) //diag to orth-diag top
      Rules += main~(0,0,11,3) | (base ~> main)~(11,0,2,0) //orth-diag top to orth-diag bottom
      Rules += main~(0,0,1,3) | (base ~> main)~(11,0,2,0) //orth-diag top to orth-diag bottom fix 1
      Rules += main~(0,0,11,3) | (base ~> main)~(2,0,2,0) //orth-diag top to orth-diag bottom fix 2
      Rules += main~(0,0,1,3) | (base ~> main)~(2,0,2,0) //orth-diag top to orth-diag bottom fix 3
      Rules += main~(11,0,2,0) | (base ~> main)~WE //orth-diag bottom to orth
      Rules += main~(11,0,2,0) | (base ~> main)~WC //orth-diag bottom to orth stub
	  
	  // Rules += main~WE | (base ~> main)~(2,2,2,2) //alternate attempt at OxO +
	  // Rules += main~(2,2,2,2) | (base ~> main)~WE //alternate attempt at OxO continue
	  // Rules += main~(2,2,2,2) | (base ~> main)~WC //alternate attempt at OxO continue stub
	  
	  Rules += main~WE | (base ~> main)~WE & (base ~> main)~NS  	// OxO
      Rules += main~WE & main~NS | (base ~> main)~WE          		// OxO continue
      Rules += main~WE & main~NS | (base ~> main)~WC          		// OxO continue stub
			  
      Rules += main~WE | (base ~> main)~WE & (base ~> main)~NC		// OxO T Thru-Side
      Rules += main~WE | (base ~> main)~WC & (base ~> main)~NS		// OxO T End-Side
      Rules += main~WE & main~NC | (base ~> main)~WE          		// OxO T Thru-Side continue
      Rules += main~WE & main~CS | (base ~> main)~WE          		// OxO T Thru-Side continue
      Rules += main~WE & main~NC | (base ~> main)~WC          		// OxO T Thru-Side continue stub
      Rules += main~WE & main~CS | (base ~> main)~WC          		// OxO T Thru-Side continue stub
		  
	  Rules += main~WE | (base ~> main)~WE & (base ~> main)~ES 		// OxD from orth
      Rules += main~WE & main~ES~SE | (base ~> main)~WE          	// OxD orth continue
      Rules += main~WE & main~ES~SE | (base ~> main)~WC          	// OxD orth continue stub
		  
	  Rules += main~ES | (base ~> main)~NW & (base ~> main)~NS      // DxO
	  Rules += main~ES & main~NS | (base ~> main)~NW       			// DxO diag continue
	  Rules += main~ES & main~NS | (base ~> main)~(3,0,0,0)       	// DxO diag stub continue
		  
      Rules += main~ES | (base ~> main)~NW & (base ~> main)~EN      // DxD
      Rules += main~SE & main~WS | (base ~> main)~NW       			// DxD continue
      Rules += main~SE & main~WS | (base ~> main)~(3,0,0,0)       	// DxD continue stub
	  
	  // non-standard self-intersections
      //from ortho
      Rules += main~WE | (base ~> main)~(2,11,2,0)
      Rules += main~WE | (base ~> main)~(2,13,2,0)
      //continuation
      Rules += main~(2,11,2,0) | (base ~> main)~WE
      Rules += main~(2,13,2,0) | (base ~> main)~WE
      Rules += main~(2,11,2,0) | (base ~> main)~WC
      Rules += main~(2,13,2,0) | (base ~> main)~WC
      
      //from ortho
      Rules += main~WE | (base ~> main)~(2,11,2,2)
      Rules += main~WE | (base ~> main)~(2,13,2,2)
      // Rules += main~WE | (base ~> main)~(2,2,2,11)
      // Rules += main~WE | (base ~> main)~(2,2,2,13)
      //continuation
      Rules += main~(2,11,2,2) | (base ~> main)~WE
      Rules += main~(2,13,2,2) | (base ~> main)~WE
      // Rules += main~(2,2,2,11) | (base ~> main)~WE
      // Rules += main~(2,2,2,13) | (base ~> main)~WE
      Rules += main~(2,11,2,2) | (base ~> main)~WC
      Rules += main~(2,13,2,2) | (base ~> main)~WC
      // Rules += main~(2,2,2,11) | (base ~> main)~WC
      // Rules += main~(2,2,2,13) | (base ~> main)~WC

	  //temp for OxD Ts until proper T-half is added
	  Rules += main~WE & main~SE | (base ~> main)~NW //initial
	  Rules += main~NS & main~NE | (base ~> main)~WS


	  
      for (minor <- CrossNetworks) {
        
        if (!minor.isNwm) {  // can't do NWM until more SAM x NWM intersection tiles are defined
          createAdjacentIntersections(main, base, minor)
        }
      
        if (base == Street) {
		  
        }

        if (isSingleTile(minor)) {
          // OxO
          Rules += main~WE | (base ~> main)~WE & minor~NS~SN          // OxO
          Rules += main~WE & minor~NS~SN | (base ~> main)~WE          // OxO continue
          Rules += main~WE & minor~NS~SN | (base ~> main)~WC          // OxO continue stub
          // OxD (to do: consider asymmetrical)
          Rules += main~WE | (base ~> main)~WE & minor~ES       // OxD
          Rules += main~WE & minor~WN | (base ~> main)~WE       // OxD continue
          Rules += main~WE & minor~WN | (base ~> main)~WC       // OxD stub
          if (!minor.isNwm) { // can't do NWM until more SAM x NWM intersection tiles are defined
            // DxO
            Rules += main~ES | (base ~> main)~NW & minor~NS       // DxO
            Rules += main~ES & minor~NS | (base ~> main)~NW       // DxO continue
            // DxD
            Rules += main~ES | (base ~> main)~NW & minor~EN       // DxD
            Rules += main~SE & minor~WS | (base ~> main)~NW       // DxD continue
          }
        }

        if (minor == Road || minor == Onewayroad/*|| minor == Dirtroad || minor == Rhw3 || minor == Mis || minor == Rhw4 || minor == Tla3 || minor == Ave2 || minor == Ard3 || minor == Owr1 || minor == Nrd4 */) { 
          //OxO T (main thru)
          Rules += main~WE | (base ~> main)~WE & minor~NC~CN
          Rules += main~WE | (base ~> main)~WE & minor~CS~SC
          //continue
          Rules += main~WE & minor~NC~CN | (base ~> main)~WE
          Rules += main~WE & minor~CS~SC | (base ~> main)~WE
          //continue stub
          Rules += main~WE & minor~NC~CN | (base ~> main)~WC
          Rules += main~WE & minor~CS~SC | (base ~> main)~WC
        }

        if(minor == Road || minor == Onewayroad/*|| minor == Dirtroad || minor == Rhw3 || minor == Mis || minor == Rhw4 || isNwm(minor)*/) { 
          //OxO T (main end)
          Rules += main~WE | (base ~> main)~WC & minor~NS
        }

        if (minor.typ == AvenueLike) {
          // OxO
          Rules += main~WE | (base ~> main)~WE & minor~NS             // OxO
          Rules += main~WE & minor~NS | (base ~> main)~WE & minor~SN  // OxO far side
          Rules += main~WE & minor~SN | (base ~> main)~WE             // OxO continue
          Rules += main~WE & minor~SN | (base ~> main)~WC             // OxO stub
          // OxD
          Rules += main~WE | (base ~> main)~WE & minor~ES                           // OxD start
          Rules += main~WE & minor~ES | (base ~> main)~WE & minor~SharedDiagRight   // OxD middle
          Rules += main~WE & minor~SharedDiagRight | (base ~> main)~WE & minor~WN   // OxD end
          Rules += main~WE & minor~WN | (base ~> main)~WE                           // OxD continue
          Rules += main~WE & minor~WN | (base ~> main)~WC                           // OxD continue stub
          // DxO
          Rules += main~ES | (base ~> main)~NW & minor~NS             // DxO start
          Rules += main~EN & minor~EW | (base ~> main)~SW & minor~EW  // DxO middle 1
          Rules += main~ES & minor~NS | (base ~> main)~NW & minor~SN  // DxO middle 2
          Rules += main~EN & minor~WE | (base ~> main)~SW & minor~WE  // DxO end
          Rules += main~ES & minor~SN | (base ~> main)~NW             // DxO continue
          Rules += main~ES & minor~SN | (base ~> main)~(3,0,0,0)      // DxO continue stub
          // DxD
          Rules += main~ES | (base ~> main)~NW & minor~NE                          // DxD start
          Rules += main~EN & minor~ES | (base ~> main)~SW & minor~SharedDiagRight  // DxD middle
          Rules += main~ES & minor~SharedDiagLeft | (base ~> main)~NW & minor~SW   // DxD end
          Rules += main~ES & minor~SW | (base ~> main)~NW                          // DxD continue
          Rules += main~ES & minor~SW | (base ~> main)~(3,0,0,0)                   // DxD continue stub
        }
		
		for(minor2 <- CrossNetworks if minor2 != main) {
          if (hasRightShoulder(minor2)) {
			Rules += main~WE & minor~SN | (base ~> main)~WE & minor2~NS       // OxO | OxO adj
		    Rules += main~WE & minor~WN | (base ~> main)~WE & minor2~NS // OxD | OxO adjacencies
		    Rules += main~WE & minor~NS | (base ~> main)~WE & minor2~WN // OxD | OxO adjacencies
          }
        }		

      }
	  		
    }
    createRules()
  }
}

// Compile individually with `sbt "runMain metarules.module.CompileSamCode"`.
object CompileSamCode extends AbstractMain {
  lazy val resolve: IdResolver = new SamResolver orElse new MiscResolver orElse new NwmResolver
  val generator = new SamRuleGenerator(_)
  lazy val file = new java.io.File("target/SAM_MetaGenerated_MANAGED.txt")
}