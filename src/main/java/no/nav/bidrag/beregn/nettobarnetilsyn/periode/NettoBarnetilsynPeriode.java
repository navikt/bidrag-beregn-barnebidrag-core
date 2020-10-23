package no.nav.bidrag.beregn.nettobarnetilsyn.periode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.nettobarnetilsyn.beregning.NettoBarnetilsynBeregning;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynGrunnlag;
import no.nav.bidrag.beregn.nettobarnetilsyn.bo.BeregnNettoBarnetilsynResultat;

public interface NettoBarnetilsynPeriode {
  BeregnNettoBarnetilsynResultat beregnPerioder(
      BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag);

  List<Avvik> validerInput(BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag);

  static NettoBarnetilsynPeriode getInstance() {
    return new NettoBarnetilsynPeriodeImpl(NettoBarnetilsynBeregning.getInstance());
  }

  HashSet<Periode> beregnSoknadbarn13aarsdagListe(
      BeregnNettoBarnetilsynGrunnlag beregnNettoBarnetilsynGrunnlag);

  Integer beregnSoknadbarnAlder(LocalDate fodselsdato, LocalDate beregnTil);

  double finnEndeligFaktiskUtgiftBelop(int alder, double faktiskUtgift);

}
