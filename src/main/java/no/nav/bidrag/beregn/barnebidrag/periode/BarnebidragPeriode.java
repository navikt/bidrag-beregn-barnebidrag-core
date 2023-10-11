package no.nav.bidrag.beregn.barnebidrag.periode;

import java.util.List;
import no.nav.bidrag.beregn.barnebidrag.beregning.BarnebidragBeregning;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragGrunnlag;
import no.nav.bidrag.beregn.barnebidrag.bo.BeregnBarnebidragResultat;
import no.nav.bidrag.beregn.felles.bo.Avvik;

public interface BarnebidragPeriode {
  BeregnBarnebidragResultat beregnPerioder(BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag);

  List<Avvik> validerInput(BeregnBarnebidragGrunnlag beregnBarnebidragGrunnlag);

  static BarnebidragPeriode getInstance() {
    return new BarnebidragPeriodeImpl(BarnebidragBeregning.Companion.getInstance());
  }
}
