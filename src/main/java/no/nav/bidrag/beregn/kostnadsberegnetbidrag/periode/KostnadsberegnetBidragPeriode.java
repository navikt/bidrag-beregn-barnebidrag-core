package no.nav.bidrag.beregn.kostnadsberegnetbidrag.periode;

import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.beregning.KostnadsberegnetBidragBeregning;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragGrunnlag;
import no.nav.bidrag.beregn.kostnadsberegnetbidrag.bo.BeregnKostnadsberegnetBidragResultat;

public interface KostnadsberegnetBidragPeriode {
  BeregnKostnadsberegnetBidragResultat beregnPerioder(
      BeregnKostnadsberegnetBidragGrunnlag beregnKostnadsberegnetBidragGrunnlag);

  List<Avvik> validerInput(BeregnKostnadsberegnetBidragGrunnlag beregnKostnadsberegnetBidragGrunnlag);

  static KostnadsberegnetBidragPeriode getInstance() {
    return new KostnadsberegnetBidragPeriodeImpl(KostnadsberegnetBidragBeregning.getInstance());
  }

  Integer beregnSoknadbarnAlder(
      BeregnKostnadsberegnetBidragGrunnlag beregnKostnadsberegnetBidragGrunnlag,
      LocalDate beregnDatoFra);

}
