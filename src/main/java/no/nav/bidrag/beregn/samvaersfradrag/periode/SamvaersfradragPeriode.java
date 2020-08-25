package no.nav.bidrag.beregn.samvaersfradrag.periode;

import java.time.LocalDate;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.samvaersfradrag.beregning.SamvaersfradragBeregning;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragGrunnlag;
import no.nav.bidrag.beregn.samvaersfradrag.bo.BeregnSamvaersfradragResultat;

public interface SamvaersfradragPeriode {
  BeregnSamvaersfradragResultat beregnPerioder(
      BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag);

  List<Avvik> validerInput(BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag);

  static SamvaersfradragPeriode getInstance() {
    return new SamvaersfradragPeriodeImpl(SamvaersfradragBeregning.getInstance());
  }

  Integer beregnSoknadbarnAlder(
      BeregnSamvaersfradragGrunnlag beregnSamvaersfradragGrunnlag,
      LocalDate beregnDatoFra);

}
