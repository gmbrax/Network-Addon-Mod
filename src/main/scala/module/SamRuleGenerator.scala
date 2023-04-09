package metarules.module

import metarules.meta._
import Network._, Flags._, Flag._, RotFlip._, Implicits._
import scala.collection.mutable.Buffer
import NetworkProperties._


class SamRuleGenerator(var context: RuleTransducer.Context) extends RuleGenerator with Adjacencies {

  def start(): Unit = {
    /*
    Generate OxO rules by iteration over list of supported crossings
    */
    val SamNetworks = List(Sam2, Sam3, Sam4, Sam5, Sam6, Sam7, Sam8, Sam9, Sam10, Sam11)

    val CrossNetworks = List(Road, Avenue, Onewayroad,
    Rail, L1Dtr, L2Dtr, Glr1, Glr2, Str, Dirtroad, Rhw3, Mis, Rhw4, Rhw6s, Rhw8sm, Rhw8s, Rhw10s, Rhw12s, Rhw6cm,
    Rhw6c, Rhw8c, L1Rhw2, L1Rhw3, L1Mis, L1Rhw4, L1Rhw6s, L1Rhw8sm, L1Rhw8s, L1Rhw10s, L1Rhw12s, L1Rhw6cm,
    L1Rhw6c, L1Rhw8c, L2Rhw2, L2Rhw3, L2Mis, L2Rhw4, L2Rhw6s, L2Rhw8sm, L2Rhw8s, L2Rhw10s, L2Rhw12s, L2Rhw6cm,
    L2Rhw6c, L2Rhw8c, L3Mis, L3Rhw4, L3Rhw6s, L4Mis, L4Rhw4, L4Rhw6s, Tla3, Ave2, Ard3, Owr1, Owr3, Nrd4, Tla5, Owr4, 
	Owr5, Rd4, Rd6, Ave6, Tla7m, Ave6m)


    for (main <- SamNetworks; base <- main.base) {

      Rules += main~WE | (base ~> main)~WE      // ortho
      Rules += main~WE | base~CW | % | main~WE  // overrides end stub to orth Sam
      Rules += main~WE | base~CE | % | main~WE  // overrides end stub to orth Sam
      Rules += main~WE | base~(0,0,0,0) | % | main~WE // overides stub to orth Sam

      Rules += Rail~CW & main~CE | (base ~> main)~WE // Orth OST Adj
      Rules += Rail~CW & main~CE | base~CW | % | main~WE
      Rules += Rail~CW & main~CE | base~CE | % | main~WE 
      Rules += Rail~CW & main~CE | base~(0,0,0,0) | % | main~WE 
      Rules += Rail~CW & main~WE | (base ~> main)~WE // Orth Ramp HT
      Rules += Rail~CW & main~WE | base~CW | % | main~WE
      Rules += Rail~CW & main~WE | base~CE | % | main~WE 
      Rules += Rail~CW & main~WE | base~(0,0,0,0) | % | main~WE

      /*
      for (minor <- CrossNetworks) {
        createAdjacentIntersections(main, base, minor)
      }
      */

      for (minor <- CrossNetworks if minor.height != main.height) {
        /*
        Cases:
        1.) 1-tile
        2.) Avenue-like
        3.) Dual-tile Asymmetrical e.g. RHW-8S
        4.) Triple-tile
        */

        if (isSingleTile(minor)) {
          // OxO
          Rules += main~WE | (base ~> main)~WE & minor~NS~SN          // OxO
          Rules += main~WE & minor~NS~SN | (base ~> main)~WE          // OxO continue
          // OxD (to do: consider asymmetrical)
          Rules += main~WE | (base ~> main)~WE & minor~ES       // OxD
          Rules += main~WE & minor~WN | (base ~> main)~WE       // OxD continue
          // DxO
          Rules += main~ES | (base ~> main)~NW & minor~NS       // DxO
          Rules += main~ES & minor~NS | (base ~> main)~NW       // DxO continue
          // DxD
          Rules += main~ES | (base ~> main)~NW & minor~EN       // DxD
          Rules += main~SE & minor~WS | (base ~> main)~NW       // DxD continue
        }

        if (minor.typ == AvenueLike) {
          // OxO
          Rules += main~WE | (base ~> main)~WE & minor~NS             // OxO
          Rules += main~WE & minor~NS | (base ~> main)~WE & minor~SN      // OxO far side
          Rules += main~WE & minor~SN | (base ~> main)~WE             // OxO continue
          // OxD
          Rules += main~WE | (base ~> main)~WE & minor~ES             // OxD start
          Rules += main~WE & minor~ES | (base ~> main)~WE & minor~SharedDiagRight                   // OxD middle
          Rules += main~WE & minor~SharedDiagRight | (base ~> main)~WE & minor~WN       // OxD end
          Rules += main~WE & minor~WN | (base ~> main)~WE             // OxD continue
          // DxO
          Rules += main~ES | (base ~> main)~NW & minor~NS                 // DxO start
          Rules += main~EN & minor~EW | (base ~> main)~SW & minor~EW      // DxO middle 1
          Rules += main~ES & minor~NS | (base ~> main)~NW & minor~SN      // DxO middle 2
          Rules += main~EN & minor~WE | (base ~> main)~SW & minor~WE      // DxO end
          Rules += main~ES & minor~SN | (base ~> main)~NW                 // DxO continue
          // DxD
          Rules += main~ES | (base ~> main)~NW & minor~NE                                           // DxD start
          Rules += main~EN & minor~ES | (base ~> main)~SW & minor~SharedDiagRight                   // DxD middle
          Rules += main~ES & minor~SharedDiagLeft | (base ~> main)~NW & minor~SW                    // DxD end
          Rules += main~ES & minor~SW | (base ~> main)~NW           // DxD continue
        }

        /*
        if (hasRightShoulder(minor)) {
          Rules += main~WE | (base ~> main)~WE & minor~NS       // OxO
          // orth continue
          Rules += main~WE & minor~SN | (base ~> main)~WE       // OxO continue
        }
        if (hasLeftShoulder(minor)) {
          Rules += main~WE | (base ~> main)~WE & minor~SN       // OxO
          // orth continue
          Rules += main~WE & minor~NS | (base ~> main)~WE       // OxO continue
          // orth OST/HT
        }
        
        for(minor2 <- CrossNetworks if minor2.height != main.height && minor2 != main) {
          if (hasRightShoulder(minor2)) {
          Rules += main~WE & minor~SN | (base ~> main)~WE & minor2~NS       // OxO | OxO adj
          Rules += main~WE & minor~SN | minor2~NS | % | main~WE & minor2~NS // OxO | OxO adj no-int
          }
        }
        Rules += main~WE | (base ~> main)~WE & minor~SE // OxD
        Rules += main~WE | minor~SE | % | main~WE & minor~SE // OxD no-int
        Rules += main~EW & minor~SW | (base ~> main)~WE // OxD continue
        for(minor2 <- CrossNetworks if minor2.height != main.height) {
          Rules += main~WE & minor~WN | (base ~> main)~WE & minor2~NS // OxD | OxO adjacencies
        }
       */
      }
    }
    createRules()
  }
}
