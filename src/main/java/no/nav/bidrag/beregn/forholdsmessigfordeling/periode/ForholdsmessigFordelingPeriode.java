package no.nav.bidrag.beregn.forholdsmessigfordeling.periode;

import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.forholdsmessigfordeling.beregning.ForholdsmessigFordelingBeregning;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingGrunnlag;
import no.nav.bidrag.beregn.forholdsmessigfordeling.bo.BeregnForholdsmessigFordelingResultat;

public interface ForholdsmessigFordelingPeriode {
  BeregnForholdsmessigFordelingResultat beregnPerioder(
      BeregnForholdsmessigFordelingGrunnlag beregnForholdsmessigFordelingGrunnlag);

  List<Avvik> validerInput(BeregnForholdsmessigFordelingGrunnlag beregnForholdsmessigFordelingGrunnlag);

  static ForholdsmessigFordelingPeriode getInstance() {
    return new ForholdsmessigFordelingPeriodeImpl(ForholdsmessigFordelingBeregning.getInstance());
  }

}
