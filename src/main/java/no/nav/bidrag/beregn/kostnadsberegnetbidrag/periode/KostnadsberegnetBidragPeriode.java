package no.nav.bidrag.beregn.barnebidrag.periode;

import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;

public interface KostnadsberegnetBidragPeriode {
  BeregnBarnebidragResultat beregnPerioder(
      BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag);

  List<Avvik> validerInput(BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag);

  static KostnadsberegnetBidragPeriode getInstance() {
    return new KostnadsberegnetBidragPeriodeImpl(BarnebidragBeregning.getInstance());
  }

  Integer beregnSoknadbarnAlder(
      BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag,
      LocalDate beregnDatoFra);

}
