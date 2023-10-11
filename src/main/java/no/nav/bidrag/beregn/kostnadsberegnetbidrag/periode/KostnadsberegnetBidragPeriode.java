package no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode;

import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnetKostnadsberegnetBidragResultat;

public interface KostnadsberegnetBidragPeriode {
  BeregnetKostnadsberegnetBidragResultat beregnPerioder(BeregnKostnadsberegnetBidragGrunnlag beregnKostnadsberegnetBidragGrunnlag);

  List<Avvik> validerInput(BeregnKostnadsberegnetBidragGrunnlag beregnKostnadsberegnetBidragGrunnlag);

  static KostnadsberegnetBidragPeriode getInstance() {
    return new KostnadsberegnetBidragPeriodeImpl(KostnadsberegnetBidragBeregning.Companion.getInstance());
  }
}
